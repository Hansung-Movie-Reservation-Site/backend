package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.ReviewLike;
import com.springstudy.backend.API.Repository.Entity.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    void deleteByUserIdAndReviewId(Long userId, Long reviewId);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    long countByReviewId(@Param("reviewId") Long reviewId);

}