package com.springstudy.backend.API.Auth.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Auth.Model.MovieDTO;
import com.springstudy.backend.API.Auth.Model.MovieDetailDTO;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.MovieRepository;
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
    private static final String API_KEY = "65a8c2b9d77ff46a7957c24a83a213a9";
    private static final String DAILY_BOX_OFFICE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private static final String MOVIE_DETAIL_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional("transactionManager")
    public void fetchAndSaveMovies() {
        String dateStr = LocalDate.now().minusDays(1).toString().replace("-", "");
        String requestUrl = DAILY_BOX_OFFICE_URL + "?key=" + API_KEY + "&targetDt=" + dateStr;

        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        JsonNode jsonNode = parseJson(response.getBody());  // ✅ JSON 파싱 예외 처리

        if (jsonNode == null) {
            System.out.println("❌ JSON 파싱 실패. API 응답을 확인하세요.");
            return;
        }

        List<Movie> movies = StreamSupport.stream(jsonNode.get("boxOfficeResult").get("dailyBoxOfficeList").spliterator(), false)
                .map(node -> objectMapper.convertValue(node, MovieDTO.class))
                .filter(dto -> dto.getMovieCode() != null && !movieRepository.existsByMovieCode(dto.getMovieCode())) // ✅ 중복 체크
                .map(this::fetchMovieDetails)
                .collect(Collectors.toList());

        movieRepository.saveAll(movies);
    }

    private JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            System.err.println("❌ JSON 파싱 오류: " + e.getMessage());
            return null;  // ✅ 예외 발생 시 `null` 반환
        }
    }

    private Movie fetchMovieDetails(MovieDTO dto) {
        String requestUrl = MOVIE_DETAIL_URL + "?key=" + API_KEY + "&movieCd=" + dto.getMovieCode();
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        JsonNode jsonNode = parseJson(response.getBody()); // ✅ JSON 파싱 예외 처리

        if (jsonNode == null) {
            System.out.println("❌ 영화 상세정보 API 파싱 실패: " + dto.getTitle());
            return null;
        }

        MovieDetailDTO detail = objectMapper.convertValue(jsonNode.get("movieInfoResult").get("movieInfo"), MovieDetailDTO.class);

        return Movie.builder()
                .title(dto.getTitle())
                .releaseDate(dto.getParsedReleaseDate())
                .movieCode(dto.getMovieCode())
                .director(detail.getDirectorNames())
                .actors(detail.getActorNames())
                .build();
    }
}