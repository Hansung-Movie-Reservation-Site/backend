package com.springstudy.backend.API.AI.Model;

import lombok.Data;

@Data
public class AIUserResponseDTO {
    private Long id;
    private Long movieId;
    private String reason;
    private Long userId;
    private String username;

    public AIUserResponseDTO(Long id, Long movieId, String reason, Long userId, String username) {
        this.id = id;
        this.movieId = movieId;
        this.reason = reason;
        this.userId = userId;
        this.username = username;
    }
}