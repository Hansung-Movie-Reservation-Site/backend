package com.springstudy.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Configuration
public class RestTemplateConfig {

    @Value("${RECOMMEND_API_KEY}")
    String RECOMMEND_API_KEY;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpEntity<Map> getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("accept", "application/json");
        httpHeaders.set("Authorization", "Bearer "+RECOMMEND_API_KEY);
        HttpEntity<Map>  httpEntity = new HttpEntity(httpHeaders);
        return httpEntity;
    }
    // 영화 추천 요청 시.
}
