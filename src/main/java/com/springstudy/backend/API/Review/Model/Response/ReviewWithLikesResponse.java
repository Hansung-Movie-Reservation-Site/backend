package com.springstudy.backend.API.Review.Model.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWithLikesResponse {
    private Long id;
    private String username;
    private double rating;
    private String review;
    private boolean spoiler;
    private LocalDate reviewDate;
    private int likeCount;

}
