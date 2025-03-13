package com.springstudy.backend.API.Movie.Model.Request;

import lombok.Getter;
import java.util.List;

@Getter
public class ReviewRequest {
    private Float rating;
    private String review;
    private Long userId;
    private Long movieId;
}