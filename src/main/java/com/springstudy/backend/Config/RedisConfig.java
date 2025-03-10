//package com.springstudy.backend.Config;
//# 🚨 배포용 코드 n3
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import java.time.Duration;
//
//@Configuration
//public class RedisConfig {
//    // 로컬개발환경에서는 자동으로 입력되며 배포는 따로 명시를 하고 사용해야 됨.
//    // 개발 시에는 전체 주석처리할 것.
//
//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .commandTimeout(Duration.ofMillis(100)) // 타임아웃 설정
//                .build();
//
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
//
//        return new LettuceConnectionFactory(serverConfig, clientConfig);
//    }
//
//    @Bean
//    public StringRedisTemplate redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
//        return new StringRedisTemplate(redisConnectionFactory);
//    }
//    // 로컬개발환경에서는 자동으로 입력되며 배포는 따로 명시를 하고 사용해야 됨.
//    // 개발 시에는 주석처리할 것.
//}
