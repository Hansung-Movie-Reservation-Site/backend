package com.springstudy.backend.Common.JWTCommon;

import com.google.gson.internal.LinkedTreeMap;
import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.OAuth.PrincipalDetails;
import com.springstudy.backend.Common.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
// JWT 만들어주는 함수
@RequiredArgsConstructor
public class JWTUtil {
    private final RedisService redisService;
    private static final SecretKey key =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
            ));
    public static String createToken(Authentication auth) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.
        AuthUser user = (AuthUser) auth.getPrincipal();

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

    public static String createToken(PrincipalDetails principalDetails) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.
        AuthUser user = principalDetails.toAuthUser();

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

    public static String createTokenToRefresh(Claims extract) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.
        String username =extract.get("username", String.class);
        List<LinkedTreeMap<String, String>> rolesMap = extract.get("authorities", List.class);
        List<String> roles = rolesMap.stream()
                .map(map -> map.get("role")) // "authority" 키를 가진 값 추출
                .collect(Collectors.toList());

        String jwt = Jwts.builder()
                .claim("username", username)
                // .claim CustomUser 정보를 저장하는 메소드.
                // .claim: 저장할 정보 추가.
                .claim("authorities", rolesMap)
                .issuedAt(new Date(System.currentTimeMillis()))
                // .issuedAt: 생성날짜를 생성하는 메소드.
                .expiration(new Date(System.currentTimeMillis() + 360000)) //유효기간 1시간
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
    public static String createRefreshToken(Authentication auth) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.
        AuthUser user = (AuthUser) auth.getPrincipal();

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
    public static String createRefreshToken(PrincipalDetails principalDetails) {
        // auth: JWT로 회원정보를 저장해야 되기 때문에.
        AuthUser user = principalDetails.toAuthUser();

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
}
