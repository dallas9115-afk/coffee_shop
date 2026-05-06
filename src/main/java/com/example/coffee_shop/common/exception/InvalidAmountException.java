package com.example.coffee_shop.common.exception;

public class InvalidAmountException extends BusinessException {

    public InvalidAmountException() {
        super(400, "INVALID_AMOUNT", "충전 금액은 1 이상이어야 합니다.");
    }
}
