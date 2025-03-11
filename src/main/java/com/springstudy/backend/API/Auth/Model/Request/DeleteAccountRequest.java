package com.springstudy.backend.API.Auth.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원탈퇴")
public record DeleteAccountRequest(
        @NotBlank
        @Schema(description = "회원탈퇴 계정")
        String email,
        @NotBlank
        @Schema(description = "확인용 비밀번호")
        String password
) {
}
