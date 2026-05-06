package com.example.coffee_shop.menu.repository;

import com.example.coffee_shop.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
