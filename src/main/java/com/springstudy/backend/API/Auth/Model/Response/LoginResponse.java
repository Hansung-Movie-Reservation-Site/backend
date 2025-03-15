package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 응답")
public record LoginResponse (
        @NotNull
        @NotBlank
        @Schema(description = "성공 여부")
        ErrorCode code,

        @Schema(description = "user 정보")
        UserDetailDTO userDetailDTO

){
}