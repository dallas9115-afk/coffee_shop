package com.example.coffee_shop.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final T data;
    private final String code;
    private final String message;

    private ApiResponse(int status, T data, String code, String message) {
        this.status = status;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, data, null, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, data, null, null);
    }

    public static ApiResponse<Void> error(int status, String code, String message) {
        return new ApiResponse<>(status, null, code, message);
    }
}
