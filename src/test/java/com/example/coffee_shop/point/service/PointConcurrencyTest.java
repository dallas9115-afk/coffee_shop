package com.example.coffee_shop.point.service;

import com.example.coffee_shop.point.dto.PointChargeRequest;
import com.example.coffee_shop.point.entity.Point;
import com.example.coffee_shop.point.repository.PointRepository;
import com.example.coffee_shop.user.entity.User;
import com.example.coffee_shop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PointConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        pointRepository.deleteAll();
        userRepository.deleteAll();

        User user = saveUser("테스트유저");
        userId = user.getId();

        savePoint(userId, 0L);
    }

    @Test
    @DisplayName("10개 스레드 동시 충전 — 최종 잔액 정확성 검증")
    void concurrentCharge_finalBalanceCorrect() throws InterruptedException {
        // given
        int threadCount = 10;
        long chargeAmount = 1000L;
        long expectedBalance = threadCount * chargeAmount;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.charge(userId, new PointChargeRequest(chargeAmount));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(point.getBalance()).isEqualTo(expectedBalance);
    }

    private User saveUser(String name) {
        try {
            var constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();
            var nameField = User.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(user, name);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void savePoint(Long userId, Long balance) {
        try {
            var constructor = Point.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Point point = constructor.newInstance();
            var userIdField = Point.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(point, userId);
            var balanceField = Point.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(point, balance);
            pointRepository.save(point);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
