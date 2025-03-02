package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "movie")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String movieCode;  // 영화진흥위원회 API에서 제공하는 영화 코드

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;  // 개봉일

    @Column(nullable = true, length = 1000)
    private String director;  // 감독

    @Column(nullable = true, length = 2000)
    private String actors;  // 출연진

    //@Column(nullable = false, columnDefinition = "TEXT")
//    private String director;
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String actress;

}