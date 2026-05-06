package com.example.coffee_shop.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("커피숍 주문 시스템 API")
                        .description("K사 서버 개발 과제 — 커피 주문 시스템")
                        .version("v1.0"));
    }
}
