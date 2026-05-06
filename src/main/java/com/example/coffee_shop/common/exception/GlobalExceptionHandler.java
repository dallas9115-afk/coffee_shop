package com.example.coffee_shop.common.exception;

import com.example.coffee_shop.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("BusinessException: [{}] {}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.error(e.getStatus(), e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("유효성 검증 실패");
        log.warn("ValidationException: {}", message);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(400, "VALIDATION_ERROR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(500, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
