package com.springstudy.backend.API.AI.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AIRecommendedMovieDTO {

    private Long movieId;
    private String title;
    private String posterImage;
    private String reason;  // ✅ 추천 이유 추가
}