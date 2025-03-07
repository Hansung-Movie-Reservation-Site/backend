package com.springstudy.backend.Config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    // env 변수를 직접 페이지에서 사용할 수 있게 설정하는 페이지.

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("RECOMMAND_API_KEY", dotenv.get("RECOMMAND_API_KEY"));
    }
}