package com.springstudy.backend.Security;

import com.springstudy.backend.Security.OAuth.OAuth2FailureHandler;
import com.springstudy.backend.Security.OAuth.OAuth2SucessHandler;
import com.springstudy.backend.Security.OAuth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
// @Configuration, @EnableWebSecurity: 스프링 시큐리티 설정에 대한 클래스이다.
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService userService;
    private final OAuth2SucessHandler oauth2SucessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;

    @Bean
    // SecurityFilterChain라는 객체를 컨테이너에 저장.
    //SecurityFilterChain: spring security가 요청을 처리할 때 이용하는 필터.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
        http
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                        .userService(userService))
                                .successHandler(oauth2SucessHandler)
                                .failureHandler(oauth2FailureHandler)
                        // 소셜로그인은 /oauth2/authorization/google에서 진행.
                );

        return http.build();
        // SecurityFilterChain을 반환.
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8000",
                "http://43.200.184.143:8080",
                "http://hs-cinemagix.duckdns.org:8080",
                "https://cinemagix-xi.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
