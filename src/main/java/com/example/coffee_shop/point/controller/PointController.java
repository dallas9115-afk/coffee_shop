package com.example.coffee_shop.point.controller;

import com.example.coffee_shop.common.response.ApiResponse;
import com.example.coffee_shop.point.dto.PointChargeRequest;
import com.example.coffee_shop.point.dto.PointResponse;
import com.example.coffee_shop.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Tag(name = "Point", description = "포인트 API")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전합니다. 분산 락으로 동시성을 보장합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "충전 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 미존재"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "충전 금액 부적절"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "락 획득 실패")
    })
    @PatchMapping("/{userId}/charge")
    public ResponseEntity<ApiResponse<PointResponse>> charge(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Valid @RequestBody PointChargeRequest request) {
        PointResponse response = pointService.charge(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
