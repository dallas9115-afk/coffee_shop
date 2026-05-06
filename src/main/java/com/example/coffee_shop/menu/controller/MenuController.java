package com.example.coffee_shop.menu.controller;

import com.example.coffee_shop.common.response.ApiResponse;
import com.example.coffee_shop.menu.dto.MenuResponse;
import com.example.coffee_shop.menu.dto.PopularMenuResponse;
import com.example.coffee_shop.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "커피 메뉴 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 목록 조회", description = "전체 커피 메뉴를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus() {
        List<MenuResponse> menus = menuService.getMenus();
        return ResponseEntity.ok(ApiResponse.ok(menus));
    }

    @Operation(summary = "인기 메뉴 조회", description = "최근 7일간 주문 횟수가 많은 상위 3개 메뉴를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularMenuResponse>>> getPopularMenus() {
        List<PopularMenuResponse> popularMenus = menuService.getPopularMenus();
        return ResponseEntity.ok(ApiResponse.ok(popularMenus));
    }
}
