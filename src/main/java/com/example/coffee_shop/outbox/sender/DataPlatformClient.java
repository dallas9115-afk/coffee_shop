package com.example.coffee_shop.outbox.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformClient {

    private final RestTemplate restTemplate;

    @Value("${data-platform.api.url}")
    private String apiUrl;

    /**
     * Outbox 이벤트 payload를 데이터 수집 플랫폼으로 전송한다.
     *
     * @param payload JSON 문자열
     * @return 전송 성공 여부
     */
    public boolean send(String payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(apiUrl, request, String.class);

            log.info("데이터 플랫폼 전송 성공: {}", payload);
            return true;
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패: {}", payload, e);
            return false;
        }
    }
}
