package com.example.coffee_shop.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Long balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 포인트 충전
     */
    public void charge(long amount) {
        this.balance += amount;
    }

    /**
     * 포인트 차감. 잔액 부족 시 false 반환.
     */
    public boolean deduct(long amount) {
        if (this.balance < amount) {
            return false;
        }
        this.balance -= amount;
        return true;
    }
}
