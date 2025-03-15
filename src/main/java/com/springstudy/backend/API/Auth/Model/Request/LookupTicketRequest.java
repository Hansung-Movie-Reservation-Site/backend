package com.springstudy.backend.API.Auth.Model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "예매 내역 조회")
public record LookupTicketRequest(
        @NotBlank
        @Schema(description = "조회할 사용자 id")
        Long user_id
){
}
