package com.example.coffee_shop.order.controller;

import com.example.coffee_shop.common.response.ApiResponse;
import com.example.coffee_shop.order.dto.OrderRequest;
import com.example.coffee_shop.order.dto.OrderResponse;
import com.example.coffee_shop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문/결제 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "커피 주문/결제", description = "메뉴를 주문하고 포인트로 결제합니다. 주문 데이터는 Outbox를 통해 비동기 전송됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 또는 메뉴 미존재"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잔액 부족"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "락 획득 실패")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> order(
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.order(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }
}
