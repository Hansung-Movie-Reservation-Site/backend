package com.springstudy.backend.API.Auth.Model.Request.ChangeDetail;

import com.springstudy.backend.API.Auth.Model.Request.ChangeRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank
        @Schema(description = "유저 아이디")
        Long user_id,
        @NotBlank
        @Schema(description = "변경 후 내용")
        String after
) implements ChangeRequest {
}
