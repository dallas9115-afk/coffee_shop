package com.example.coffee_shop.order.service;

import com.example.coffee_shop.common.exception.MenuNotFoundException;
import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.menu.entity.Menu;
import com.example.coffee_shop.menu.repository.MenuRepository;
import com.example.coffee_shop.order.dto.OrderRequest;
import com.example.coffee_shop.order.dto.OrderResponse;
import com.example.coffee_shop.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private OrderExecutor orderExecutor;

    @Mock
    private RLock rLock;

    @Mock
    private Menu menu;

    @Test
    @DisplayName("주문 성공")
    void order_success() throws Exception {
        // given
        OrderRequest request = new OrderRequest(1L, 3L);
        OrderResponse expected = new OrderResponse(1L, 1L, 3L, "바닐라라떼", 5500, 44500L, LocalDateTime.now());

        given(userRepository.existsById(1L)).willReturn(true);
        given(menuRepository.findById(3L)).willReturn(Optional.of(menu));
        given(menu.getName()).willReturn("바닐라라떼");
        given(menu.getPrice()).willReturn(5500);
        given(redissonClient.getLock(anyString())).willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(orderExecutor.execute(1L, 3L, "바닐라라떼", 5500)).willReturn(expected);

        // when
        OrderResponse result = orderService.order(request);

        // then
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.menuName()).isEqualTo("바닐라라떼");
        assertThat(result.price()).isEqualTo(5500);
        verify(orderExecutor).execute(1L, 3L, "바닐라라떼", 5500);
    }

    @Test
    @DisplayName("존재하지 않는 유저 주문 시 UserNotFoundException")
    void order_userNotFound() {
        // given
        OrderRequest request = new OrderRequest(999L, 1L);
        given(userRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> orderService.order(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 주문 시 MenuNotFoundException")
    void order_menuNotFound() {
        // given
        OrderRequest request = new OrderRequest(1L, 999L);
        given(userRepository.existsById(1L)).willReturn(true);
        given(menuRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.order(request))
                .isInstanceOf(MenuNotFoundException.class);
    }
}
