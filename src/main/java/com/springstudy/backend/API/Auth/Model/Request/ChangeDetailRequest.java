package com.springstudy.backend.API.Auth.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 정보 변경 비밀번호, 이메일, 유저이름 중 요청")
public record ChangeDetailRequest(
        @NotBlank
        @Schema(description = "유저 이메일")
        String email,
        @NotBlank
        @Schema(description = "비밀번호 확인")
        String password,
        @NotBlank
        @Schema(description = "변경 후 내용")
        String after
) {
}