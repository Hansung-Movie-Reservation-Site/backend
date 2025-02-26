package com.springstudy.backend.API.Auth.Controller;


import com.springstudy.backend.API.Auth.Service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/movies")
public class MovieControllerV1 {

    private final MovieService movieService;

    public MovieControllerV1(MovieService movieService) {
        this.movieService = movieService;
    }

    // 📌 API 실행 트리거 (수동 실행)
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchMovies() {
        movieService.fetchAndSaveMovies();
        return ResponseEntity.ok("✅ 영화 데이터가 업데이트되었습니다.");
    }
}