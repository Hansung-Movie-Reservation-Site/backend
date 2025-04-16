package com.springstudy.backend.API.Auth.Model.Request.AccountRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 요청")
public record LoginRequest (
        @NotNull
        @NotBlank
        @Schema(description = "로그인 이메일")
        String email,

        @NotNull
        @NotBlank
        @Schema(description = "회원가입 비밀번호")
        String password
){
}