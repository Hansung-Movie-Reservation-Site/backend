package com.springstudy.backend.API.Review.Controller;

import com.springstudy.backend.API.Repository.Entity.Review;
import com.springstudy.backend.API.Review.Model.Request.ReviewRequest;
import com.springstudy.backend.API.Review.Model.Response.ReviewLikeResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewStringResponse;
import com.springstudy.backend.API.Review.Service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/review")

public class ReviewControllerV1 {

    private final ReviewService reviewService;

    public ReviewControllerV1(ReviewService reviewService) {this.reviewService = reviewService;}

    //리뷰 작성
    @PostMapping("/reviews")
    public ResponseEntity<String> saveReview(@RequestBody ReviewRequest review) {
        reviewService.saveReview(review);
        return ResponseEntity.ok("리뷰 작성 완료");
    }

    //movieId를 입력한 영화의 평균 별점 조회
    @GetMapping("/rating")
    public ResponseEntity<ReviewResponse> getRating(@RequestParam Long movieId) {
        ReviewResponse response = reviewService.getAverageRatingByMovieId(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getReviewsByMovie")
    public List<ReviewStringResponse> getReviewsByMovie(@RequestParam Long movieId) {
        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);
        return reviews.stream()
                .map(r -> new ReviewStringResponse(
                        r.getId(),
                        r.getRating(),
                        r.getReview(),
                        r.getUser().getUsername(),
                        r.getSpoiler(),
                        r.getReviewDate()
                ))
                .toList();
    }

    @PostMapping("/likeToggle")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestParam Long userId, @RequestParam Long reviewId) {
        boolean liked = reviewService.toggleReviewLike(userId, reviewId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviewId", reviewId);
        response.put("userId", userId);
        response.put("liked", liked);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getLikeCount")
    public ResponseEntity<ReviewLikeResponse> getLikeCount(@RequestParam Long reviewId) {
        ReviewLikeResponse response = reviewService.getLikeCount(reviewId);
        return ResponseEntity.ok(response);
    }

    //리뷰, 좋아요 통합해서 반환해줌
    @GetMapping("/review/list/reviewWithLikes")
    public ResponseEntity<?> getReviewsWithLikes(@RequestParam Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewWithLikes(movieId));
    }
}
