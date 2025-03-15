package com.springstudy.backend.API.Auth.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "추천영화 조회")
public record LookupRecommandRequest (
        @Schema(description = "조회할 회원 id")
        @NotBlank
        long id
){
}
