package com.springstudy.backend.API.Auth.Model.Response.ChangeResponse;

import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 정보 변경 응답")
public record ChangeDetailResponse(
        @NotBlank
        @Schema(description = "응답 코드")
        ErrorCode code
) {
}
