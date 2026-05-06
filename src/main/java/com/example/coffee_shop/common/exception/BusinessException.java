package com.example.coffee_shop.common.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final int status;
    private final String code;

    protected BusinessException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
