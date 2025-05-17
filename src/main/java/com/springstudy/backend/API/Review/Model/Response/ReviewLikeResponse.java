package com.springstudy.backend.API.Review.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ReviewLikeResponse {
    private int likeCount;
    private long reviewId;
}
