package com.springstudy.backend.Security.OAuth;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes; // getAttributes()

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderEmail() {
        Object object = attributes.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap) object;
        return (String) accountMap.get("email");
    }

    @Override
    public String getProviderName() {
        Object propertiesObj = attributes.get("properties");
        if (propertiesObj == null) return null;

        LinkedHashMap properties = (LinkedHashMap) propertiesObj;
        return (String) properties.get("nickname"); // nickname 가져오기
    }
}