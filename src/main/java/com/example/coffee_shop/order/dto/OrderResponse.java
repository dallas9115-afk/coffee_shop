package com.example.coffee_shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "주문 응답")
public record OrderResponse(
    @Schema(description = "주문 ID", example = "1")
    Long orderId,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "메뉴 ID", example = "3")
    Long menuId,

    @Schema(description = "메뉴 이름", example = "바닐라라떼")
    String menuName,

    @Schema(description = "결제 금액", example = "5500")
    Integer price,

    @Schema(description = "결제 후 잔액", example = "44500")
    Long remainingBalance,

    @Schema(description = "주문 시각")
    LocalDateTime orderedAt
) {}
