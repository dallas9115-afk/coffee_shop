package com.example.coffee_shop.menu.service;

import com.example.coffee_shop.menu.dto.MenuResponse;
import com.example.coffee_shop.menu.dto.PopularMenuResponse;
import com.example.coffee_shop.menu.repository.MenuRepository;
import com.example.coffee_shop.order.dto.PopularMenuProjection;
import com.example.coffee_shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    @Cacheable(value = "menus")
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Cacheable(value = "popularMenus")
    public List<PopularMenuResponse> getPopularMenus() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<PopularMenuProjection> projections = orderRepository.findPopularMenus(since);

        List<PopularMenuResponse> result = new ArrayList<>();
        for (int i = 0; i < projections.size(); i++) {
            PopularMenuProjection p = projections.get(i);
            result.add(new PopularMenuResponse(
                    i + 1,
                    p.getMenuId(),
                    p.getMenuName(),
                    p.getOrderCount()
            ));
        }
        return result;
    }
}
