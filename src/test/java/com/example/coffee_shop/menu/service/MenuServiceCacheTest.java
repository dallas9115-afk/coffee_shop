package com.example.coffee_shop.menu.service;

import com.example.coffee_shop.menu.dto.PopularMenuProjection;
import com.example.coffee_shop.menu.entity.Menu;
import com.example.coffee_shop.menu.repository.MenuRepository;
import com.example.coffee_shop.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(MenuServiceCacheTest.TestConfig.class)
class MenuServiceCacheTest {

    @EnableCaching
    @Configuration
    @Import(MenuService.class)
    static class TestConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("menus", "popularMenus");
        }

        @Bean
        public MenuRepository menuRepository() {
            return Mockito.mock(MenuRepository.class);
        }

        @Bean
        public OrderRepository orderRepository() {
            return Mockito.mock(OrderRepository.class);
        }
    }

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear()
        );
        Mockito.reset(menuRepository, orderRepository);
    }

    @Test
    @DisplayName("getMenus — 두 번째 호출 시 캐시 적중하여 DB 조회하지 않음")
    void getMenus_cacheHit() {
        // given
        Menu menu = Menu.builder().name("아메리카노").price(4500).build();
        given(menuRepository.findAll()).willReturn(List.of(menu));

        // when
        menuService.getMenus();
        menuService.getMenus();

        // then
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPopularMenus — 두 번째 호출 시 캐시 적중하여 DB 조회하지 않음")
    void getPopularMenus_cacheHit() {
        // given
        PopularMenuProjection projection = mock(PopularMenuProjection.class);
        given(projection.getMenuId()).willReturn(1L);
        given(projection.getMenuName()).willReturn("아메리카노");
        given(projection.getOrderCount()).willReturn(10L);
        given(orderRepository.findPopularMenus(any())).willReturn(List.of(projection));

        // when
        menuService.getPopularMenus();
        menuService.getPopularMenus();

        // then
        verify(orderRepository, times(1)).findPopularMenus(any());
    }

    @Test
    @DisplayName("getPopularMenus 결과에 순위가 올바르게 부여됨")
    void getPopularMenus_rankAssignment() {
        // given
        PopularMenuProjection p1 = mock(PopularMenuProjection.class);
        given(p1.getMenuId()).willReturn(1L);
        given(p1.getMenuName()).willReturn("아메리카노");
        given(p1.getOrderCount()).willReturn(30L);

        PopularMenuProjection p2 = mock(PopularMenuProjection.class);
        given(p2.getMenuId()).willReturn(2L);
        given(p2.getMenuName()).willReturn("카페라떼");
        given(p2.getOrderCount()).willReturn(20L);

        given(orderRepository.findPopularMenus(any())).willReturn(List.of(p1, p2));

        // when
        var result = menuService.getPopularMenus();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).menuName()).isEqualTo("아메리카노");
        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).menuName()).isEqualTo("카페라떼");
    }
}
