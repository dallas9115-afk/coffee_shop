package com.example.coffee_shop.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 20)
    private String status; // COMPLETED / CANCELLED

    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDateTime.now();
    }

    @Builder
    public Order(Long userId, Long menuId, Integer price) {
        this.userId = userId;
        this.menuId = menuId;
        this.price = price;
        this.status = "COMPLETED";
    }
}
