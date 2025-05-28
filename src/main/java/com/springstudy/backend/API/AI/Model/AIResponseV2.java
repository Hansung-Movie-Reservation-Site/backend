package com.springstudy.backend.API.AI.Model;

import com.springstudy.backend.API.Repository.Entity.AI;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "gpt가 추천 영화")
public record AIResponseV2(

        @Schema(description = "성공여부")
        @NotBlank
        ErrorCode code,

        @Schema(description = "추천 영화 정보")
        @NotNull
        Movie movie,

        @Schema(description = "추천 이유")
        String reason
) {
}
