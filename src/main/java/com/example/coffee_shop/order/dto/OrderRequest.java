package com.example.coffee_shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "주문 요청")
public record OrderRequest(
    @Schema(description = "주문 사용자 ID", example = "1")
    @NotNull(message = "사용자 ID는 필수입니다.")
    Long userId,

    @Schema(description = "주문 메뉴 ID", example = "3")
    @NotNull(message = "메뉴 ID는 필수입니다.")
    Long menuId
) {}
