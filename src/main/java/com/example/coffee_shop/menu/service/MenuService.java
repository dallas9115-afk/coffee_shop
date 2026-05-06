package com.example.coffee_shop.menu.service;

import com.example.coffee_shop.menu.dto.MenuResponse;
import com.example.coffee_shop.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Cacheable(value = "menus")
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .toList();
    }
}
