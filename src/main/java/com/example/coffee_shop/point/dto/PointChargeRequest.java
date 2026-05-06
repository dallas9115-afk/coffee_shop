package com.example.coffee_shop.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "포인트 충전 요청")
public record PointChargeRequest(
    @Schema(description = "충전 금액 (1 이상)", example = "10000")
    @NotNull(message = "충전 금액은 필수입니다.")
    @Positive(message = "충전 금액은 1 이상이어야 합니다.")
    Long amount
) {}
