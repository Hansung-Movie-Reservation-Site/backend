package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByMovieId(Integer movieId);
    // boolean existsByMovieId(Integer movieId);

    // boolean existsByMovieCode(String movieCode);

    // ✅ 영화 제목에 특정 문자열이 포함된 데이터 조회 (대소문자 구분 X)
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
}