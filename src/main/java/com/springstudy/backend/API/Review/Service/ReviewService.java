package com.springstudy.backend.API.Review.Service;

import com.springstudy.backend.API.Repository.Entity.ReviewLike;
import com.springstudy.backend.API.Repository.ReviewLikeRepository;
import com.springstudy.backend.API.Review.Model.Request.ReviewRequest;
import com.springstudy.backend.API.Review.Model.Response.ReviewLikeResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewResponse;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Review;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.MovieRepository;
import com.springstudy.backend.API.Repository.ReviewRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewResponse getAverageRatingByMovieId(Long movieId) {
        Double averageRating = reviewRepository.findAverageRatingByMovieId(movieId);
        if(averageRating == null) {
            averageRating = 0.0;
        }
        return new ReviewResponse(movieId, averageRating);
    }

    @Transactional
    public void saveReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MOVIE));

        // 별점 검증
        if (request.getRating() < 0.0 || request.getRating() > 5.0) {
            throw new CustomException(ErrorCode.INVALID_RATING);
        }

        // 리뷰 저장
        Review review = Review.builder()
                .rating(request.getRating())
                .review(request.getReview())
                .spoiler(request.getSpoiler())
                .reviewDate(LocalDate.now())
                .user(user)
                .movie(movie)
                .build();
        reviewRepository.save(review);
    }

    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieIdOrderByIdDesc(movieId);
    }

    @Transactional
    public boolean toggleReviewLike(Long userId, Long reviewId) {
        boolean alreadyLiked = reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);

        if (alreadyLiked) {
            reviewLikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
            return false; // 좋아요 취소됨
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"));

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("리뷰 없음"));

            ReviewLike like = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();

            reviewLikeRepository.save(like);
            return true; // 좋아요 추가됨
        }
    }

    public ReviewLikeResponse getLikeCount(Long reviewId) {
        int likeCount = reviewLikeRepository.countByReviewId(reviewId);
        return new ReviewLikeResponse(likeCount, reviewId);
    }
}