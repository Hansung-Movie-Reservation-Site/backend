package com.springstudy.backend.API.Movie.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Movie.Model.MovieDTO;
import com.springstudy.backend.API.Movie.Model.MovieDetailDTO;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.TMDB_API_KEY}")
    private static String TMDB_API_KEY;
    private static final String DISCOVER_MOVIE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=" + TMDB_API_KEY + "&language=ko-KR&region=KR&primary_release_date.gte=%s&primary_release_date.lte=%s";
    private static final String MOVIE_DETAIL_URL = "https://api.themoviedb.org/3/movie/%d?api_key=" + TMDB_API_KEY + "&language=ko-KR&append_to_response=credits";

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void fetchAndSaveMovies() {
        String startDate = LocalDate.now().minusDays(60).toString();
        String endDate = LocalDate.now().plusDays(7).toString();
        String requestUrl = String.format(DISCOVER_MOVIE_URL, startDate, endDate);

        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody()).get("results");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Movie> movies = StreamSupport.stream(jsonNode.spliterator(), false)
                .map(node -> objectMapper.convertValue(node, MovieDTO.class))
                .map(this::fetchMovieDetails)
                .filter(movie -> movie != null)  // ✅ `null` 체크 후 저장
                .filter(movie -> !isDuplicate(movie))  // ✅ 중복된 영화 제외
                .collect(Collectors.toList());

        // ✅ MySQL에 데이터 저장 및 로그 출력
        if (!movies.isEmpty()) {
            movieRepository.saveAll(movies);
            movies.forEach(movie -> System.out.println("✅ 저장 완료: " + movie.getTitle() + " (" + movie.getReleaseDate() + ")"));
        } else {
            System.out.println("❌ 저장할 영화가 없음.");
        }
    }

    private Movie fetchMovieDetails(MovieDTO dto) {
        ResponseEntity<String> response = restTemplate.getForEntity(String.format(MOVIE_DETAIL_URL, dto.getMovieId()), String.class);
        MovieDetailDTO detail = null;
        try {
            detail = objectMapper.readValue(response.getBody(), MovieDetailDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // ✅ 제목, 줄거리, 장르, 감독, 나이 제한, 상영 시간 값이 `null` 또는 공백이면 저장하지 않음
        if (isEmpty(dto.getTitle())
                || isEmpty(detail.getOverview())
                || isEmpty(detail.getGenreNames())
                || isEmpty(detail.getDirectorNames())
                ) {
            System.out.println("❌ 필수 데이터가 없거나 공백이어서 저장하지 않음: " + dto.getMovieId());
            return null;
        }

        return Movie.builder()
                .movieId(dto.getMovieId())
                .title(dto.getTitle())
                .releaseDate(dto.getParsedReleaseDate())
                .overview(detail.getOverview())  // ✅ 한국어 개요
                .director(detail.getDirectorNames())  // ✅ 한국어 감독 이름
                .genres(detail.getGenreNames())  // ✅ 한국어 장르
                .posterImage(detail.getFullPosterUrl())
                .runtime(detail.getRuntime())  // ✅ 상영 시간 추가
                .build();
    }

    /**
     * ✅ 영화 제목에 특정 문자열이 포함된 영화 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Movie> searchMoviesByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println(keyword); // 문자열 null 발생
            throw new IllegalArgumentException("❌ 검색어를 입력하세요.");
        }

        return movieRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * ✅ `null`이거나 공백 문자열인지 확인하는 유틸리티 메서드
     */
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * ✅ 영화가 이미 저장되어 있는지 확인하는 메서드
     */
    private boolean isDuplicate(Movie movie) {
        boolean exists = movieRepository.existsByMovieId(movie.getMovieId());
        if (exists) {
            System.out.println("⚠️ 이미 저장된 영화: " + movie.getTitle() + " (" + movie.getReleaseDate() + ")");
        }
        return exists;
    }
}