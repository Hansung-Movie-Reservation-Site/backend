package com.springstudy.backend.API.Payment.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoReadyResponse {
    private String uuid;
    private String tid;
    private String next_redirect_app_url;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String ios_app_scheme;
    private String created_at;
}


