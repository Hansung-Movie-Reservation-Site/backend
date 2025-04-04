package com.springstudy.backend.API.AI.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "사용자 예매내역 전달(4점 이상)")
public record AIRequest(
        @Schema(description = "유저 id")
        //@NotNull
        Long user_id
) {
}
