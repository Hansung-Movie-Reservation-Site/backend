package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Ticket;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record LookupTicketResponse (
        @NotBlank
        @Schema(description = "성공 여부")
        ErrorCode code,

        @Schema(description = "티켓 정보")
        List<Ticket> ticketList
){
}
