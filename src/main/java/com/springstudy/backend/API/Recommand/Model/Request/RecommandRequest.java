package com.springstudy.backend.API.Recommand.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "영화 추천 요청")
public record RecommandRequest (
        @Schema(description = "영화 이름")
        @NotBlank
        Long movie_id
){
}

