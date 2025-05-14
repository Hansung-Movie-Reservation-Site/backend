package com.springstudy.backend.API.Movie.Controller;


import com.springstudy.backend.API.Movie.Service.MovieService;
import com.springstudy.backend.API.Repository.Entity.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
public class MovieControllerV1 {

    private final MovieService movieService;

    public MovieControllerV1(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * ✅ TMDB API를 이용하여 영화 정보 가져와 저장
     * GET /api/v1/movies/fetch
     */
    @PostMapping("/fetch")
    public ResponseEntity<List<Movie>> fetchTMDBMovies() {
        try {
            List<Movie> movies = movieService.fetchAndSaveMoviesByTMDB();
            return ResponseEntity.ok(movies);
            // return ResponseEntity.ok("✅ 영화 데이터가 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ KOBIS API를 이용하여 일간 박스오피스 영화 정보 가져와 저장
     * GET /api/v1/movies/daily?date=YYYYMMDD
     */
    @GetMapping("/daily")
    public ResponseEntity<List<Movie>> fetchAndSaveDailyMovies() {
        try {
            List<Movie> movies = movieService.fetchAndSaveDailyBoxOfficeMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ 영화 제목 검색 API
     * GET /api/v1/movies/search?keyword=어벤져스
     */
    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam String keyword) {
        try {
            List<Movie> movies = movieService.searchMoviesByTitle(keyword);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/searchById")
    public ResponseEntity<Movie> searchMovies(@RequestParam Long id) {
        try {
            Movie movie = movieService.searchMoviesById(id);
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}