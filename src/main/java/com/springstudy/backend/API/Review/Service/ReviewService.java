package com.springstudy.backend.API.Review.Service;

import com.springstudy.backend.API.Repository.Entity.ReviewLike;
import com.springstudy.backend.API.Repository.ReviewLikeRepository;
import com.springstudy.backend.API.Review.Model.Request.ReviewRequest;
import com.springstudy.backend.API.Review.Model.Request.ReviewUpdateRequest;
import com.springstudy.backend.API.Review.Model.Response.ReviewDTO;
import com.springstudy.backend.API.Review.Model.Response.ReviewLikeResponse;
import com.springstudy.backend.API.Review.Model.Response.ReviewResponse;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Review;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.MovieRepository;
import com.springstudy.backend.API.Repository.ReviewRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.API.Review.Model.Response.ReviewWithLikesResponse;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Review saveReview(ReviewRequest request) {
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
       return reviewRepository.save(review);
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

    public List<ReviewWithLikesResponse> getReviewWithLikes(Long movieId, Long userId) {
        List<Review> reviews = reviewRepository.findByMovieIdOrderByIdDesc(movieId);

        // 좋아요 수 조회
        List<Object[]> likeResults = reviewLikeRepository.countLikesByMovieId(movieId);
        Map<Long, Integer> likeMap = likeResults.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).intValue()
                ));

        // 현재 사용자가 누른 좋아요 리스트 조회
        Set<Long> likedReviewIds = reviewLikeRepository.findLikedReviewIdsByUserId(userId);

        return reviews.stream()
                .map(r -> new ReviewWithLikesResponse(
                        r.getId(),
                        r.getUser().getUsername(),
                        r.getRating(),
                        r.getReview(),
                        r.getSpoiler(),
                        r.getReviewDate(),
                        likeMap.getOrDefault(r.getId(), 0),
                        likedReviewIds.contains(r.getId())
                ))
                .toList();
    }

    public boolean getUserLikedReview(Long userId, Long reviewId) {
        return reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    public List<ReviewDTO> getAllReviews(){
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        ReviewDTO reviewDTO;
        for(int i = 0; i<reviews.size(); i++){
            Float rate = reviews.get(i).getRating();
            String review = reviews.get(i).getReview();
            boolean spoiler = reviews.get(i).getSpoiler();
            String title = reviews.get(i).getMovie().getTitle();
            String username = reviews.get(i).getUser().getUsername();
            String poster = reviews.get(i).getMovie().getPosterImage();
            reviewDTO = new ReviewDTO(username, rate, review, title, spoiler, poster);
            reviewDTOS.add(reviewDTO);
        }

        LogUtil.error(this.getClass() ,"review 가져오기 성공. 총 review 개수: "+ reviewDTOS.size());
        return reviewDTOS;
    }

    @Transactional
    public void updateReview(Long reviewId, Long userId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        // 작성자만 수정 가능하게 체크
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 별점, 내용, 스포일러 여부 수정
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getReview() != null) review.setReview(request.getReview());
        if (request.getSpoiler() != null) review.setSpoiler(request.getSpoiler());
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        // 1. 리뷰 존재 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        // 2. 본인 리뷰만 삭제 가능
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        // 3. ReviewLike 먼저 삭제 (좋아요 기록)
        reviewLikeRepository.deleteAllByReviewId(reviewId);

        // 4. 리뷰 삭제
        reviewRepository.deleteById(reviewId);
    }
}