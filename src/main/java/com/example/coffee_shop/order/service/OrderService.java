package com.example.coffee_shop.order.service;

import com.example.coffee_shop.common.exception.LockAcquisitionException;
import com.example.coffee_shop.common.exception.MenuNotFoundException;
import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.menu.entity.Menu;
import com.example.coffee_shop.menu.repository.MenuRepository;
import com.example.coffee_shop.order.dto.OrderRequest;
import com.example.coffee_shop.order.dto.OrderResponse;
import com.example.coffee_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final RedissonClient redissonClient;
    private final OrderExecutor orderExecutor;

    private static final String LOCK_KEY_PREFIX = "point:lock:";
    private static final long WAIT_TIME = 5L;
    private static final long LEASE_TIME = 3L;

    /**
     * 주문/결제 (분산 락 적용)
     *
     * 락 키는 PointService와 동일한 point:lock:{userId}를 사용한다.
     * 충전과 주문이 같은 유저에 대해 동시에 실행될 때 잔액 정합성을 보장하기 위함이다.
     */
    public OrderResponse order(OrderRequest request) {
        // 1. 유저 존재 확인 (락 획득 전 빠른 실패)
        if (!userRepository.existsById(request.userId())) {
            throw new UserNotFoundException(request.userId());
        }

        // 2. 메뉴 존재 확인 + 가격 조회 (락 획득 전 빠른 실패)
        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new MenuNotFoundException(request.menuId()));

        // 3. 분산 락 획득 → 트랜잭션 실행
        String lockKey = LOCK_KEY_PREFIX + request.userId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!acquired) {
                throw new LockAcquisitionException();
            }

            try {
                return orderExecutor.execute(
                        request.userId(),
                        request.menuId(),
                        menu.getName(),
                        menu.getPrice()
                );
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException();
        }
    }
}
