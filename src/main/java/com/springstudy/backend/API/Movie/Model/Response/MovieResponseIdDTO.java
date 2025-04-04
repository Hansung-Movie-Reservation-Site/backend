package com.springstudy.backend.API.Movie.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieResponseIdDTO {
    private Long movieId;       // ✅ 추가: movieId 포함
    private String title;
    private String posterImage;
}
