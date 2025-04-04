package com.springstudy.backend.API.AI.Model;

import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "gpt가 추천 영화")
public record AIResponse(

        @Schema(description = "성공여부")
        @NotBlank
        ErrorCode errorCode,

        @Schema(description = "추천 영화 id")
        @NotNull
        Long movie_id,

        @Schema(description = "추천 이유")
        String aiResponse
) {
}
