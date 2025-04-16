package com.springstudy.backend.API.Auth.Model.Response.AccountResponse;

import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeleteAccountResponse(
        @NotBlank
        @Schema(description = "회원 탈퇴 여부")
        ErrorCode code
) {
}
