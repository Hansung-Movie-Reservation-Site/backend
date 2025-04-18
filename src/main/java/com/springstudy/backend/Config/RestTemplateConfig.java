package com.springstudy.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {
    @Value("${api.GPT_API_KEY}")
    private String openAiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate gptRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request,body,execution)->{
            request.getHeaders().add("Authorization","Bearer "+openAiKey);
            request.getHeaders().add("Content-Type","application/json");
            return execution.execute(request, body); // ❌ 이 코드는 바로 실행되지 않음
        });
        return restTemplate;
    }
}
