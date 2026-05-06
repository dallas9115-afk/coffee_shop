package com.example.coffee_shop.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인기 메뉴 응답")
public record PopularMenuResponse(
    @Schema(description = "순위", example = "1")
    Integer rank,

    @Schema(description = "메뉴 ID", example = "1")
    Long menuId,

    @Schema(description = "메뉴 이름", example = "아메리카노")
    String menuName,

    @Schema(description = "주문 횟수", example = "142")
    Long orderCount
) {}
