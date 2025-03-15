package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Recommand;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record LookupRecommandResponse(
        @Schema(description = "성공 여부")
        @NotBlank
        ErrorCode code,

        @Schema(description = "추천 영화 목록")
        List<Recommand> recommandList
) {
}
