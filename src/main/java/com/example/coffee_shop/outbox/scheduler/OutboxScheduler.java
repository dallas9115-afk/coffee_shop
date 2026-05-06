package com.example.coffee_shop.outbox.scheduler;

import com.example.coffee_shop.outbox.entity.OutboxEvent;
import com.example.coffee_shop.outbox.repository.OutboxEventRepository;
import com.example.coffee_shop.outbox.sender.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final DataPlatformClient dataPlatformClient;

    /**
     * 5초 간격으로 PENDING 이벤트를 폴링하여 전송한다.
     * fixedDelay: 이전 실행 완료 후 5초 대기. 동시 실행 없음.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollAndSend() {
        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Outbox 폴링: {}건 PENDING 이벤트 발견", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            boolean success = dataPlatformClient.send(event.getPayload());

            if (success) {
                event.markSent();
                log.info("Outbox 이벤트 전송 완료: id={}, aggregateId={}",
                        event.getId(), event.getAggregateId());
            } else {
                event.markFailed();
                log.warn("Outbox 이벤트 전송 실패: id={}, aggregateId={}",
                        event.getId(), event.getAggregateId());
            }
        }
    }
}
