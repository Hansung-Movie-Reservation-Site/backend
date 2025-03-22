package com.springstudy.backend.API.Screening.Controller;

import com.springstudy.backend.API.Movie.Model.Response.MovieResponseDTO;
import com.springstudy.backend.API.Movie.Model.Response.MovieResponseIdDTO;
import com.springstudy.backend.API.Screening.Response.SeatResponseDTO;
import com.springstudy.backend.API.Screening.Service.ScreeningService;
import com.springstudy.backend.API.Repository.Entity.Screening;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/screening")
public class ScreeningControllerV1 {

    private final ScreeningService screeningService;

    public ScreeningControllerV1(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    /**
     * ✅ 날짜, Spot 이름, (선택적) 영화 ID를 이용하여 Screening 데이터 조회
     * GET /api/v1/screening?spotName=강남&date=2025-02-20
     * GET /api/v1/screening?spotName=강남&date=2025-02-20&movieId=1
     */
    @GetMapping
    public ResponseEntity<List<Screening>> getScreenings(
            @RequestParam String spotName,
            @RequestParam LocalDate date,
            @RequestParam(required = false) Long movieId) {

        try {
            List<Screening> screenings;

            if (movieId != null) {
                // ✅ movieId가 주어진 경우
                screenings = screeningService.getScreeningsBySpotDateAndMovieId(spotName, date, movieId);
            } else {
                // ✅ movieId가 없는 경우 (spotName, date만 주어진 경우)
                screenings = screeningService.getScreeningsBySpotAndDate(spotName, date);
            }

            return ResponseEntity.ok(screenings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ 특정 날짜와 Spot 이름을 이용하여 Screening 데이터 중 영화 제목만 반환
     * GET /api/v1/screening/movieTitles?date=2025-02-20&spotName=강남
     */
    @GetMapping("/movieTitles")
    public ResponseEntity<List<String>> getMovieTitles(@RequestParam String spotName, @RequestParam LocalDate date) {
        try {
            List<String> movieTitlesBySpotAndDate = screeningService.getMovieTitlesBySpotAndDate(spotName, date);

            return ResponseEntity.ok(movieTitlesBySpotAndDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ 특정 날짜와 Spot 이름을 이용하여 영화명 + 포스터 URL 반환
     * GET /api/v1/screening/movieTitleAndPoster?date=2025-02-20&spotName=강남
     */
    @GetMapping("/movieTitleAndPoster")
    public ResponseEntity<List<MovieResponseDTO>> getMovies(@RequestParam String spotName, @RequestParam LocalDate date) {
        try {
            List<MovieResponseDTO> movies = screeningService.getMoviesBySpotAndDate(spotName, date);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ 현재 상영 중인 모든 영화 목록 반환 (movieId 포함)
     */
    @GetMapping("/allmovies")
    public ResponseEntity<List<MovieResponseIdDTO>> getAllScreeningMovies() {
        return ResponseEntity.ok(screeningService.getAllScreeningMovies());
    }

    /**
     * ✅ 특정 문자열을 포함하는 영화 제목의 상영 정보 조회
     */
    @GetMapping("/search")
    public ResponseEntity<?> getScreeningsByMovieTitle(@RequestParam String title) {
        try {
            List<Screening> screenings = screeningService.getScreeningsByMovieTitle(title);
            return ResponseEntity.ok(screenings);
        } catch (Exception e) {
            // return ResponseEntity.badRequest().body(null);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * ✅ Screening ID를 이용하여 Seat 목록 조회 (예약 여부 포함)
     * GET /api/v1/screening/{screeningId}/seats
     */
    @GetMapping("/{screeningId}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByScreeningId(@PathVariable Long screeningId) {
        try {
            List<SeatResponseDTO> seats = screeningService.getSeatsByScreeningId(screeningId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
