package com.example.coffee_shop.common.exception;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super(404, "USER_NOT_FOUND", "존재하지 않는 사용자입니다. (userId: " + userId + ")");
    }
}
