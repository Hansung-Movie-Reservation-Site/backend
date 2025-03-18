package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // tmdb movie id로 데이터 존재 확인
    boolean existsByTmdbMovieId(Integer tmdbMovieId);

    Optional<Movie> findByTmdbMovieId(Integer tmdbMovieId);

    // kobis movie cd로 데이터 조회, 중복 확인용
    Optional<Movie> findByKobisMovieCd(String kobisMovieCd);

    /**
     * insert문 대신 update문이 실행되도록 수정
     * @param kobisMovieCd
     * @param tmdbMovieId
     */
    @Transactional
    @Modifying
    @Query("UPDATE Movie m SET m.kobisMovieCd = :kobisMovieCd WHERE m.tmdbMovieId = :tmdbMovieId")
    void updateKobisMovieCdByTmdbMovieId(@Param("kobisMovieCd") String kobisMovieCd, @Param("tmdbMovieId") Integer tmdbMovieId);

    // ✅ 영화 제목에 특정 문자열이 포함된 데이터 조회 (대소문자 구분 X)
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
}