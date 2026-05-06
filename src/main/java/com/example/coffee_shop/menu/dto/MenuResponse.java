package com.example.coffee_shop.menu.dto;

import com.example.coffee_shop.menu.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메뉴 응답")
public record MenuResponse(
    @Schema(description = "메뉴 ID", example = "1")
    Long id,

    @Schema(description = "메뉴 이름", example = "아메리카노")
    String name,

    @Schema(description = "가격 (원)", example = "4500")
    Integer price
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu.getId(), menu.getName(), menu.getPrice());
    }
}
