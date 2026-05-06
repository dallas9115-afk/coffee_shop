package com.example.coffee_shop.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 응답")
public record PointResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "현재 잔액", example = "60000")
    Long balance
) {}
