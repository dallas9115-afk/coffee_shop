package com.example.coffee_shop.order.service;

import com.example.coffee_shop.common.exception.InsufficientBalanceException;
import com.example.coffee_shop.common.exception.UserNotFoundException;
import com.example.coffee_shop.order.dto.OrderResponse;
import com.example.coffee_shop.order.entity.Order;
import com.example.coffee_shop.order.repository.OrderRepository;
import com.example.coffee_shop.outbox.entity.OutboxEvent;
import com.example.coffee_shop.outbox.repository.OutboxEventRepository;
import com.example.coffee_shop.point.entity.Point;
import com.example.coffee_shop.point.entity.PointHistory;
import com.example.coffee_shop.point.repository.PointHistoryRepository;
import com.example.coffee_shop.point.repository.PointRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderExecutor {

    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponse execute(Long userId, Long menuId, String menuName, Integer price) {
        // 1. 포인트 차감
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!point.deduct(price.longValue())) {
            throw new InsufficientBalanceException(point.getBalance(), price.longValue());
        }

        // 2. 포인트 이력 기록
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .type("USE")
                .amount(price.longValue())
                .balanceAfter(point.getBalance())
                .build();
        pointHistoryRepository.save(history);

        // 3. 주문 생성
        Order order = Order.builder()
                .userId(userId)
                .menuId(menuId)
                .price(price)
                .build();
        orderRepository.save(order);

        // 4. Outbox 이벤트 생성
        String payload = buildPayload(order);
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("ORDER")
                .aggregateId(order.getId())
                .payload(payload)
                .build();
        outboxEventRepository.save(outboxEvent);

        // 5. 응답 반환
        return new OrderResponse(
                order.getId(),
                userId,
                menuId,
                menuName,
                price,
                point.getBalance(),
                order.getOrderedAt()
        );
    }

    private String buildPayload(Order order) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "orderId", order.getId(),
                    "userId", order.getUserId(),
                    "menuId", order.getMenuId(),
                    "price", order.getPrice(),
                    "orderedAt", order.getOrderedAt().toString()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Outbox payload 직렬화 실패", e);
        }
    }
}
