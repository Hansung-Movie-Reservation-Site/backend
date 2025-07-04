package com.springstudy.backend.API.Review.Model.Request;

import lombok.Getter;

@Getter
public class ReviewRequest {
    private Float rating;
    private String review;
    private Boolean spoiler;
    private Long userId;
    private Long movieId;
}