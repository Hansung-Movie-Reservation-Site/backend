package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.ReviewLike;
import com.springstudy.backend.API.Repository.Entity.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    void deleteByUserIdAndReviewId(Long userId, Long reviewId);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    int countByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT rl.review.id FROM ReviewLike rl WHERE rl.user.id = :userId")
    Set<Long> findLikedReviewIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT rl.review.id, COUNT(rl) FROM ReviewLike rl WHERE rl.review.movie.id = :movieId GROUP BY rl.review.id")
    List<Object[]> countLikesByMovieId(@Param("movieId") Long movieId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    void deleteAllByReviewId(@Param("reviewId") Long reviewId);

    void deleteByUserId(Long userId);
}