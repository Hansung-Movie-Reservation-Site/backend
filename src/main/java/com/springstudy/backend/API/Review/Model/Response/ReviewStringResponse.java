package com.springstudy.backend.API.Review.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewStringResponse {
    private Long id;
    private Float rating;
    private String review;
    private String username;
    private Boolean spoiler;
}