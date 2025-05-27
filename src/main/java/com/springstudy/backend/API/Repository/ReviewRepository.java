package com.springstudy.backend.API.Repository;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    //특정 유저의 모든 리뷰
    List<Review> findByUserId(Long userId);
    //특정 영화의 모든 리뷰
    List<Review> findByMovieIdOrderByIdDesc(Long movieId);
    //특정 영화에 특정 유저가 쓴 리뷰
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);

    // 특정 영화의 평균 평점 조회
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);
}