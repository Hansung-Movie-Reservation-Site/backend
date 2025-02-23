package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "성공 여부")
public record EmailVerifyResponse(
        @NotBlank
        @Schema(description = "인증 여부")
        ErrorCode code
){
}
