package com.springstudy.backend.API.Recommand.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "영화 추천 응답")
public record RecommandResponse (
        @Schema(description = "추천 성공 여부")
        @NotBlank
        ErrorCode code,

        @Schema(description = "추천 영화")
        @NotBlank
        Movie movie
){
}
