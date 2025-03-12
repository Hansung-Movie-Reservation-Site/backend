package com.springstudy.backend.API.Movie.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long movieId;
    private double averageRating;
}