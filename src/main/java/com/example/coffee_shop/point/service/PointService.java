package com.example.coffee_shop.point.service;

import com.example.coffee_shop.common.exception.InvalidAmountException;
import com.example.coffee_shop.common.exception.LockAcquisitionException;
import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.point.dto.PointChargeRequest;
import com.example.coffee_shop.point.dto.PointResponse;
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
public class PointService {

    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final PointChargeExecutor pointChargeExecutor;

    private static final String LOCK_KEY_PREFIX = "point:lock:";
    private static final long WAIT_TIME = 5L;
    private static final long LEASE_TIME = -1L;

    /**
     * 포인트 충전 (분산 락 적용)
     */
    public PointResponse charge(Long userId, PointChargeRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        if (request.amount() <= 0) {
            throw new InvalidAmountException();
        }

        String lockKey = LOCK_KEY_PREFIX + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!acquired) {
                throw new LockAcquisitionException();
            }

            try {
                return pointChargeExecutor.execute(userId, request.amount());
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
