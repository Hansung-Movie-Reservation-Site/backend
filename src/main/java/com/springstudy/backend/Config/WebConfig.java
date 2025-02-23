package com.springstudy.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// Configuration: 설정 관련 클래스 명시. application.yml 파일에 환경변수 가져와서 사용 가능.
public class WebConfig implements WebMvcConfigurer {
    // WebMvcConfigurer: addCorsMappings로 CORS 설정하는 인터페이스.
    @Override
    public void addCorsMappings(CorsRegistry registry){
        // addCorsMappings: CorsRegistry의 속성을 관리하여 CORS 설정을 하는 메소드.
        // CorsRegistry: CORS 관리하는 클래스.
        registry.addMapping("/**") // addMapping: 어떤 엔드포인트에 CORS를 허용할지.
                .allowedOrigins("http://localhost:3000") // allowedOriginPatterns: 어떤 클라이언트 주소 요청을 허용할지.
                .allowedMethods("GET","POST","PUT","DELETE") // allowedMethods: CORS가 허용하는 메소드 설정.
                .allowedHeaders("*") // allowedHeaders: CORS가 허용하는 요청 헤더 설정.
                .allowCredentials(true) // allowCredentials: 요청 시 인증 정보를 포함할 것을 허용.
                .maxAge(3600); // maxAge: preflight 설정.
    }
}
