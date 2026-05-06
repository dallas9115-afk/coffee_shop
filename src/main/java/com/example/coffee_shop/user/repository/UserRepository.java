package com.example.coffee_shop.user.repository;

import com.example.coffee_shop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
