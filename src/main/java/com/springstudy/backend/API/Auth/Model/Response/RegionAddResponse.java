package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "지역 추가 응답")
public record RegionAddResponse(
        @NotNull
        @NotBlank
        @Schema(description = "성공 여부")
        ErrorCode code
){
}