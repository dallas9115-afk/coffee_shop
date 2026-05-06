package com.example.coffee_shop.point.service;

import com.example.coffee_shop.common.exception.InvalidAmountException;
import com.example.coffee_shop.common.exception.LockAcquisitionException;
import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.point.dto.PointChargeRequest;
import com.example.coffee_shop.point.dto.PointResponse;
import com.example.coffee_shop.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private PointChargeExecutor pointChargeExecutor;

    @Mock
    private RLock rLock;

    @Test
    @DisplayName("포인트 충전 성공")
    void charge_success() throws Exception {
        // given
        Long userId = 1L;
        PointChargeRequest request = new PointChargeRequest(10000L);
        PointResponse expected = new PointResponse(userId, 60000L);

        given(userRepository.existsById(userId)).willReturn(true);
        given(redissonClient.getLock(anyString())).willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(pointChargeExecutor.execute(userId, 10000L)).willReturn(expected);

        // when
        PointResponse result = pointService.charge(userId, request);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(60000L);
        verify(pointChargeExecutor).execute(userId, 10000L);
    }

    @Test
    @DisplayName("존재하지 않는 유저 충전 시 UserNotFoundException")
    void charge_userNotFound() {
        // given
        Long userId = 999L;
        PointChargeRequest request = new PointChargeRequest(10000L);
        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> pointService.charge(userId, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("충전 금액 0 이하 시 InvalidAmountException")
    void charge_invalidAmount() {
        // given
        Long userId = 1L;
        PointChargeRequest request = new PointChargeRequest(0L);
        given(userRepository.existsById(userId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> pointService.charge(userId, request))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    @DisplayName("락 획득 실패 시 LockAcquisitionException")
    void charge_lockFailed() throws Exception {
        // given
        Long userId = 1L;
        PointChargeRequest request = new PointChargeRequest(10000L);

        given(userRepository.existsById(userId)).willReturn(true);
        given(redissonClient.getLock(anyString())).willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> pointService.charge(userId, request))
                .isInstanceOf(LockAcquisitionException.class);
    }
}
