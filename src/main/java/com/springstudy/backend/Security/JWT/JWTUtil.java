package com.springstudy.backend.Security.JWT;

import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.Security.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
// JWT 만들어주는 함수
@RequiredArgsConstructor
public class JWTUtil {
    private static final SecretKey key =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
            ));

    public static String createToken(AuthUser user) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.

        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                // .claim CustomUser 정보를 저장하는 메소드.
                // .claim: 저장할 정보 추가.
                .claim("authorities", user.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                // .issuedAt: 생성날짜를 생성하는 메소드.
                .expiration(new Date(System.currentTimeMillis() + 10)) //유효기간 1시간
                // .expiration: 만료기간을 설정하는 메소드.
                .signWith(key)
                .compact();
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }

    public static String createRefreshToken(AuthUser user) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.

        String refreshJwt = Jwts.builder()
                .claim("username", user.getUsername())
                // .claim CustomUser 정보를 저장하는 메소드.
                // .claim: 저장할 정보 추가.
                .claim("authorities", user.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                // .issuedAt: 생성날짜를 생성하는 메소드.
                .expiration(new Date(System.currentTimeMillis() + 1000000)) //유효기간 1시간
                // .expiration: 만료기간을 설정하는 메소드.
                .signWith(key)
                .compact();
        return refreshJwt;
    }

    public static Cookie createCookie(String name, String jwt){
        Cookie cookie = new Cookie(name, jwt);
        cookie.setHttpOnly(true);   // XSS 공격 방지
        //cookie.setSecure(true);     // HTTPS 환경에서만 쿠키 전달 -> 배포시 true 해야 됨.
        cookie.setPath("/");        // 전체 경로에서 쿠키 사용 가능
        cookie.setMaxAge(1000000); // 1일
        //cookie.setAttribute("SameSite", "None");  // 크로스 사이트 요청 허용
        cookie.setSecure(false);             // HTTPS 아닌 경우 false로 해야 동작
        cookie.setAttribute("SameSite", "Lax"); // or "Strict" (크로스 사이트 안되면 Strict도 가능)
        return cookie;
    }
}
