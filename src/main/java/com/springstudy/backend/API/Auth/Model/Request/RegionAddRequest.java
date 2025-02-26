package com.springstudy.backend.API.Auth.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "지역 추가 요청")
public record RegionAddRequest(

        @NotNull
        @NotBlank
        @Schema(description = "지역 이름")
        String name

){
}
