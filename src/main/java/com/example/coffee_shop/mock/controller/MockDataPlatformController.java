package com.example.coffee_shop.mock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mock/data-platform")
public class MockDataPlatformController {

    @PostMapping
    public ResponseEntity<String> receive(@RequestBody String payload) {
        log.info("[Mock 데이터 플랫폼] 수신: {}", payload);
        return ResponseEntity.ok("received");
    }
}
