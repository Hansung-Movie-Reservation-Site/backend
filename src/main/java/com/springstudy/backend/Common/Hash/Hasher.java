package com.springstudy.backend.Common.Hash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Hasher {
    private static PasswordEncoder passwordEncoder;

    @Autowired
//    static 필드 주입이 필요한 경우
//    Hasher처럼 유틸성 클래스에서 Spring의 빈을 활용해야 하는 경우
    public Hasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static String hash(String password) {
        return passwordEncoder.encode(password);
    }
    public static boolean matches(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
