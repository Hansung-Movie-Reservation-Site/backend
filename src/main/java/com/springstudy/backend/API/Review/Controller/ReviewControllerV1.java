package com.springstudy.backend.API.Review.Controller;

import com.springstudy.backend.API.Repository.Entity.Review;
import com.springstudy.backend.API.Review.Model.Request.ReviewRequest;
import com.springstudy.backend.API.Review.Model.Response.ReviewLikeResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewStringResponse;
import com.springstudy.backend.API.Review.Service.ReviewService;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public ResponseEntity<Map<String, Object>> saveReview(@RequestBody ReviewRequest review) {
        Long savedId = reviewService.saveReview(review);
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedId);
        result.put("message", "리뷰 작성 완료");
        return ResponseEntity.ok(result);
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
    @GetMapping("/reviewWithLikes")
    public ResponseEntity<?> getReviewsWithLikes(@RequestParam Long movieId, @RequestParam Long userId) {
        return ResponseEntity.ok(reviewService.getReviewWithLikes(movieId, userId));
    }

    //해당 유저가 해당 리뷰에 좋아요를 눌렀는지 bool 타입을 반환해줌
    @GetMapping("/isLiked")
    public ResponseEntity<Boolean> isLikedByUser(@RequestParam Long userId, @RequestParam Long reviewId) {
        boolean isLiked = reviewService.getUserLikedReview(userId, reviewId);
        return ResponseEntity.ok(isLiked);
    }
}
