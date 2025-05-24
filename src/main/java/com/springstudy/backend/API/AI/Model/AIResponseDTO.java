package com.springstudy.backend.API.AI.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIResponseDTO {
    private Long id;
    private Long movieId;
    private String reason;
    private Long userId;
    private String username;
}