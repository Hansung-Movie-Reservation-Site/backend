package com.springstudy.backend.Security.OAuth;

import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Repository.Entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {
    private User user;
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // oauth 로그인
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    public AuthUser toAuthUser(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        AuthUser authUser = new AuthUser(user.getUsername(), user.getUser_credentional().getPassword(), authorities, user.getEmail());
        return authUser;
    }

    // .. 생략 기존 UserDetails를 구현한 곳

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return null;
    }
}