package com.example.coffee_shop.order.repository;

import com.example.coffee_shop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
