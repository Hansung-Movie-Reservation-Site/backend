package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer tmdbMovieId;  // TMDB 영화 ID

    @Column(unique = true)
    private String kobisMovieCd;  // kobis 영화 ID

    @Column(nullable = false)
    private String title;  // 영화 제목

    @Column(nullable = true, length = 500)
    private String posterImage;  // ✅ 포스터 이미지 링크

    @Column(nullable = true, length = 5000)
    private String overview;  // ✅ 개요

    @Column(nullable = true, length = 1000)
    private String director;  // ✅ 감독

    @Column(nullable = true, length = 1000)
    private String genres;  // ✅ 장르

    @Column(nullable = false)
    private LocalDate releaseDate;  // ✅ 개봉일

    @Column(nullable = true)
    private Integer runtime;  // ✅ 상영 시간 (단위: 분)
}