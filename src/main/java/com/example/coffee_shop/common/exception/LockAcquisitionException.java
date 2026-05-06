package com.example.coffee_shop.common.exception;

public class LockAcquisitionException extends BusinessException {

    public LockAcquisitionException() {
        super(409, "LOCK_ACQUISITION_FAILED", "다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
    }
}
