package com.springstudy.backend.API.Review.Model.Request;

import lombok.Getter;

@Getter
public class ReviewUpdateRequest {
    private Float rating;     // 별점
    private String review;    // 리뷰 내용
    private Boolean spoiler;  // 스포일러 여부
}
