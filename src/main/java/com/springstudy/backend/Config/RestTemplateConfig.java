package com.springstudy.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpEntity<String> getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("accept", "application/json");
        httpHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MjA5YWNmZGE5MmIxNjhmZmYwOTg1OGJkMGU1OTBlMSIsIm5iZiI6MTc0MTI2ODc3MS4yODMsInN1YiI6IjY3YzlhNzIzMmU3MjkxMzA2ZjI0YjZmOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.IBbV0iXCsKILrgzKxZfRZBwMbWsqW7OAoQLwazbxQI0");
        HttpEntity<String>  httpEntity = new HttpEntity(httpHeaders);
        return httpEntity;
    }
    // 영화 추천 요청 시.
}
