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

    /**
     * rank가 예약어라서 변수명을 다르게 설정
     */
    @Column(nullable = true)
    private Integer boxOfficeRank;  // ✅ 박스오피스 순위

    @Column(nullable = true)
    private Integer rankInten; // 전일 대비 랭크 증가 수치

    @Column(nullable = true)
    private String rankOldAndNew; // 새로 랭크에 진입했는지 알려주는 문자열

    @Column(nullable = true)
    private Integer audiAcc; // 총 관객 수

    @Column(nullable = true)
    private String full_video_link; // 영화 예고편 유튜브 링크

    @Column(nullable = true)
    private LocalDate fetchedDate; // 영화 데이터를 가져온 날짜


}