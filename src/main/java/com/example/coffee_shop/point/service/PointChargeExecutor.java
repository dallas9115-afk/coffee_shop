package com.example.coffee_shop.point.service;

import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.point.dto.PointResponse;
import com.example.coffee_shop.point.entity.Point;
import com.example.coffee_shop.point.entity.PointHistory;
import com.example.coffee_shop.point.repository.PointHistoryRepository;
import com.example.coffee_shop.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointChargeExecutor {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointResponse execute(Long userId, Long amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        point.charge(amount);

        PointHistory history = PointHistory.builder()
                .userId(userId)
                .type("CHARGE")
                .amount(amount)
                .balanceAfter(point.getBalance())
                .build();

        pointHistoryRepository.save(history);

        return new PointResponse(userId, point.getBalance());
    }
}
