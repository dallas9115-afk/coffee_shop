package com.example.coffee_shop.common.exception;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException(long currentBalance, long requiredAmount) {
        super(400, "INSUFFICIENT_BALANCE",
              "잔액이 부족합니다. (현재: " + currentBalance + "P, 필요: " + requiredAmount + "P)");
    }
}
