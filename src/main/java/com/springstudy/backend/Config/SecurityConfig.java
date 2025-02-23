package com.springstudy.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// @Configuration, @EnableWebSecurity: 스프링 시큐리티 설정에 대한 클래스이다.
public class SecurityConfig {

    @Bean
    // SecurityFilterChain라는 객체를 컨테이너에 저장.
    //SecurityFilterChain: spring security가 요청을 처리할 때 이용하는 필터.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf 기능을 비활성화한다.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/**").permitAll()
                );
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        // JWT 방식을 사용할 것이므로 세션 생성을 못하게 한다.
        //permitAll(): "/**" -> 모든 페이지에 관해 로그인 없이 접근하도록 한다.
        return http.build();
        // SecurityFilterChain을 반환.
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 스프링시큐리티에서 자동적으로 비밀번호를 암호화 시킨다.
}