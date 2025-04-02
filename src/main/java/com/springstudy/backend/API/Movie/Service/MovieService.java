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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.KOBIS_API_KEY}")
    private String KOBIS_API_KEY;

    @Value("${api.TMDB_API_KEY}")
    private String TMDB_API_KEY;

    private static final String KOBIS_BOX_OFFICE_URL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private static final String KOBIS_MOVIE_DETAIL_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    private static final String TMDB_SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=";
    private static final String TMDB_DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?api_key=";

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * TMDB API만 이용하여 영화 정보 가져오기
     */
    @Transactional
    public List<Movie> fetchAndSaveMoviesByTMDB() {
        String DISCOVER_MOVIE_URL = TMDB_DISCOVER_URL + TMDB_API_KEY +
                "&language=ko-KR" +
                "&region=KR" +
                "&with_original_language=ko" +
                "&primary_release_date.gte=%s" +
                "&primary_release_date.lte=%s" +
                "&sort_by=release_date.desc";


        String startDate = LocalDate.now().minusDays(50).toString();
        String endDate = LocalDate.now().plusDays(7).toString();

        System.out.println("\n\n============================================================");
        System.out.println("시작 날짜 : " + startDate + " ~~ " + "종료 날짜 : " + endDate);
        System.out.println("============================================================");

        String requestUrl = String.format(DISCOVER_MOVIE_URL, startDate, endDate);

        // System.out.println(requestUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody()).get("results");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // ✅ 최종 저장할 영화 리스트
        List<Movie> finalMovies = new ArrayList<>();

        for (JsonNode node : jsonNode) {
            MovieDTO dto = objectMapper.convertValue(node, MovieDTO.class);

            // ✅ 1. TMDB movieId 기준으로 DB에서 영화 조회
            Optional<Movie> existingMovie = movieRepository.findByTmdbMovieId(dto.getTmdbMovieId());

            if (existingMovie.isPresent()) {
                System.out.println("✅ 이미 존재하는 영화 (TMDB 기준): " + existingMovie.get().getTitle());
                finalMovies.add(existingMovie.get());
                continue;
            }

            // ✅ 2. 존재하지 않는 경우, TMDB 상세 정보 조회 후 새로운 영화 생성
            Movie newMovie = fetchMovieDetailsTMDB(dto);

            // ✅ 3. `newMovie`가 `null`인 경우 저장하지 않고 건너뛰기
            if (newMovie == null) {
                continue;
            }

            // ✅ 3. 새 영화 저장 후 반환 리스트에 추가
            movieRepository.save(newMovie);
            // System.out.println("✅ 새롭게 저장된 영화: " + newMovie.getTitle());
            finalMovies.add(newMovie);
        }

        return finalMovies; // ✅ 최종 저장된 영화 리스트 반환

//        List<Movie> movies = StreamSupport.stream(jsonNode.spliterator(), false)
//                .map(node -> objectMapper.convertValue(node, MovieDTO.class))
//                .map(this::fetchMovieDetailsTMDB)
//                .filter(movie -> movie != null)  // ✅ `null` 체크 후 저장
//                .filter(movie -> !isDuplicate(movie))  // ✅ 중복된 영화 제외
//                .collect(Collectors.toList());
//
//        // ✅ MySQL에 데이터 저장 및 로그 출력
//        if (!movies.isEmpty()) {
//            movieRepository.saveAll(movies);
//            movies.forEach(movie -> System.out.println("✅ 저장 완료: " + movie.getTitle() + " (" + movie.getReleaseDate() + ")"));
//        } else {
//            System.out.println("❌ 저장할 영화가 없음.");
//        }
    }

    private Movie fetchMovieDetailsTMDB(MovieDTO dto) {
        String MOVIE_DETAIL_URL = "https://api.themoviedb.org/3/movie/%d?api_key=" + TMDB_API_KEY + "&language=ko-KR&append_to_response=credits";

        String realUrl = String.format(MOVIE_DETAIL_URL, dto.getTmdbMovieId());
        // System.out.println(realUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(realUrl, String.class);
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
                || isEmpty(detail.getFullPosterUrl())
                ) {
            // System.out.println("=========================================");
//            System.out.println("❌ 필수 데이터가 없거나 공백이어서 저장하지 않음\n"
//                    + "제목 : " + dto.getTitle() + "\n"
//                    + "개요 : " + detail.getOverview() + "\n"
//                    + "장르 : " + detail.getGenreNames() + "\n"
//                    + "감독 : " + detail.getDirectorNames() + "\n"
//                    + "포스터 : " + detail.getFullPosterUrl());
            System.out.println("❌ 필수 데이터가 없거나 공백이어서 저장하지 않음 : " + dto.getTitle());
            // System.out.println("=========================================");
            return null;
        }

        Movie m = Movie.builder()
                .tmdbMovieId(dto.getTmdbMovieId())
                .title(dto.getTitle())
                .releaseDate(dto.getParsedReleaseDate())
                .overview(detail.getOverview())  // ✅ 한국어 개요
                .director(detail.getDirectorNames())  // ✅ 한국어 감독 이름
                .genres(detail.getGenreNames())  // ✅ 한국어 장르
                .posterImage(detail.getFullPosterUrl())
                .runtime(detail.getRuntime())  // ✅ 상영 시간 추가
                .build();

        System.out.println("🎬 ========= 새롭게 저장하는 영화 정보 ===========");
        System.out.println("✅ 제목: " + m.getTitle());
        System.out.println("🆔 TMDB ID: " + m.getTmdbMovieId());
        System.out.println("📆 개봉일: " + m.getReleaseDate());
        System.out.println("🕒 러닝타임: " + m.getRuntime() + "분");
        System.out.println("🎭 장르: " + m.getGenres());
        System.out.println("👨‍🎬 감독: " + m.getDirector());
        System.out.println("🖼️ 포스터: " + m.getPosterImage());
        System.out.println("📝 줄거리: " + m.getOverview());
        System.out.println();
        System.out.println("🎞️ KOBIS 영화 코드: " + m.getKobisMovieCd());
        System.out.println("박스오피스 순위 : " + m.getBoxOfficeRank());
        System.out.println("============================================");


        return m;
    }


    /**
     * ✅ KOBIS API에서 일간 박스오피스 영화 목록 가져오기 & TMDB API에서 포스터 검색 후 저장
     */
    @Transactional
    public List<Movie> fetchAndSaveDailyBoxOfficeMovies() {

        System.out.println("\n\n============================================================");
        System.out.println("박스오피스 날짜 : " + LocalDate.now().minusDays(1));
        System.out.println("============================================================");

        // ✅ 어제 날짜 구하기 (YYYYMMDD 형식)
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String url = KOBIS_BOX_OFFICE_URL + "?key=" + KOBIS_API_KEY + "&targetDt=" + targetDate;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("KOBIS API 응답이 비어 있습니다.");
        }

        List<Map<String, Object>> movieList = (List<Map<String, Object>>) ((Map<String, Object>) response.getBody().get("boxOfficeResult")).get("dailyBoxOfficeList");


        // System.out.println("---------------------------------------------------");
        // System.out.println("movieList : " + movieList);
        // System.out.println("---------------------------------------------------");
//        List<Movie> movies = movieList.stream()
//                .map(movieData -> fetchMovieDetailsAndConvert(movieData))
//                .filter(movie -> movieRepository.findByKobisMovieCd(movie.getKobisMovieCd()).isEmpty())  // 중복 제거
//                .collect(Collectors.toList());
//
//        return movieRepository.saveAll(movies);  // 저장 후 반환

        // ✅ 결과를 저장할 리스트
        List<Movie> finalMovies = new ArrayList<>();

        for (Map<String, Object> movieData : movieList) {
            String kobisMovieCd = (String) movieData.get("movieCd");
            Integer rank = Integer.parseInt((String) movieData.get("rank"));


            // System.out.println("kobisMovieCd : " + kobisMovieCd);
            // System.out.println("rank : " + rank);
            // ✅ 1. KOBIS movieCd 기준으로 조회
            Optional<Movie> existingMovieByKobis = movieRepository.findByKobisMovieCd(kobisMovieCd);

            if (existingMovieByKobis.isPresent()) {
                // System.out.println("✅ 이미 존재하는 영화 (KOBIS 기준): " + existingMovieByKobis.get().getTitle());
                //finalMovies.add(existingMovieByKobis.get());

                Movie movie = existingMovieByKobis.get();
                movie.setBoxOfficeRank(rank); // ✅ rank 업데이트
                movieRepository.save(movie); // ✅ 업데이트 반영
                System.out.println("✅ 기존 영화 rank 업데이트: " + movie.getTitle() + " → " + rank + "위");
                finalMovies.add(movie);

                continue;
            }

            // ✅ 2. 존재하지 않는 경우, TMDB 정보 조회 후 새로운 영화 생성
            Movie newMovie = fetchMovieDetailsAndConvert(movieData);

            // ✅ 3. TMDB movieId 기준으로 중복된 영화 조회
            Optional<Movie> existingMovieByTmdb = movieRepository.findByTmdbMovieId(newMovie.getTmdbMovieId());

            /**
             * kobisMovieCd 값이 없지만 tmdbMovieId값이 이미 존재하는 경우
             */
            if (existingMovieByTmdb.isPresent()) {
                // ✅ 기존 데이터의 kobisMovieCd 값만 업데이트
                // movieRepository.updateKobisMovieCdByTmdbMovieId(kobisMovieCd, newMovie.getTmdbMovieId());
                // System.out.println("✅ 기존 영화 업데이트 (KOBIS 코드 변경): " + existingMovieByTmdb.get().getTitle());



                // ✅ 변경 사항 반영 후 다시 조회
                // Movie updatedMovie = movieRepository.findByTmdbMovieId(newMovie.getTmdbMovieId()).get();

                // ✅ 필드 전체 덮어쓰기 (id 제외)
                // BeanUtils.copyProperties(newMovie, existingMovieByTmdb, "id");

                // 필드 덮어쓰기
                Movie existing = existingMovieByTmdb.get();

                existing.setKobisMovieCd(newMovie.getKobisMovieCd());
                existing.setTitle(newMovie.getTitle());
                existing.setBoxOfficeRank(newMovie.getBoxOfficeRank());
                existing.setReleaseDate(newMovie.getReleaseDate());
                existing.setTmdbMovieId(newMovie.getTmdbMovieId());
                existing.setPosterImage(newMovie.getPosterImage());
                existing.setOverview(newMovie.getOverview());
                existing.setDirector(newMovie.getDirector());
                existing.setRuntime(newMovie.getRuntime());
                existing.setGenres(newMovie.getGenres());

                movieRepository.save(existing);
                finalMovies.add(existing);
            } else {
                // ✅ 4. 새롭게 영화 저장
                movieRepository.save(newMovie);
                // System.out.println("✅ 새롭게 저장된 영화: " + newMovie.getTitle());
                finalMovies.add(newMovie);
            }
        }

        return finalMovies;  // ✅ 최종 10개 영화 반환
    }

    /**
     * ✅ KOBIS 상세 영화 정보를 가져오고 TMDB API 호출 후 Movie 엔티티로 변환
     */
    private Movie fetchMovieDetailsAndConvert(Map<String, Object> movieData) {
        String kobisMovieCd = (String) movieData.get("movieCd");

        String detailUrl = KOBIS_MOVIE_DETAIL_URL + "?key=" + KOBIS_API_KEY + "&movieCd=" + kobisMovieCd;
        ResponseEntity<Map> detailResponse = restTemplate.getForEntity(detailUrl, Map.class);


        if (detailResponse.getBody() == null || !detailResponse.getBody().containsKey("movieInfoResult")) {
            throw new RuntimeException("KOBIS 상세 정보 API 응답이 비어 있습니다.");
        }

        Map<String, Object> movieInfo = (Map<String, Object>) ((Map<String, Object>) detailResponse.getBody().get("movieInfoResult")).get("movieInfo");

        // System.out.println(movieInfo);

        String movieNmEn = (String) movieInfo.getOrDefault("movieNmEn", "");  // 영어 제목

        // System.out.println("movieNmEn : " + movieNmEn);

        /**
         * 국문 영화 제목
         * 영문 영화 제목으로 영화를 구분할 수 없을 때 필요
         */
        String movieNm = (String) movieInfo.getOrDefault("movieNm", "");  // 영어 제목

        // System.out.println("movieNm : " + movieNm);

        String director = movieInfo.containsKey("directors") && !((List) movieInfo.get("directors")).isEmpty()
                ? (String) ((Map<String, Object>) ((List) movieInfo.get("directors")).get(0)).get("peopleNm")
                : "";

        // System.out.println("director : " + director);

        String genres = movieInfo.containsKey("genres") && !((List) movieInfo.get("genres")).isEmpty()
                ? (String) ((Map<String, Object>) ((List) movieInfo.get("genres")).get(0)).get("genreNm")
                : "";

        // System.out.println("genres : " + genres);

        String releaseDate = (String) movieInfo.getOrDefault("openDt", "");

        // System.out.println("openDt : " + releaseDate);

        // TMDB API를 통해 추가 정보 조회
        Map<String, Object> tmdbData = fetchTmdbMovieInfo(movieNm, movieNmEn, releaseDate);

        // System.out.println(tmdbData);

        // System.out.println("--------------------------------");

        // System.out.println("--------------------------------------------");
        // System.out.println("boxofficeRank : " + movieData.get("rank"));
        // System.out.println("---------------------------------------------");

        Movie a = Movie.builder()
                .kobisMovieCd(kobisMovieCd)
                .title((String) movieData.get("movieNm"))
                .boxOfficeRank(Integer.parseInt((String) movieData.get("rank")))
                .releaseDate(releaseDate.isEmpty() ?
                        LocalDate.parse((String) tmdbData.getOrDefault("release_date", null), DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                        LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .tmdbMovieId((Integer) tmdbData.getOrDefault("id", null))
                .posterImage((String) tmdbData.getOrDefault("poster_path", null))
                .overview((String) tmdbData.getOrDefault("overview", null))
                .director(director.isEmpty() ?
                        (String) tmdbData.getOrDefault("director", null)
                        : director)
                .runtime((Integer) tmdbData.getOrDefault("runtime", null))  // ✅ 상영 시간 추가
                .genres(genres)
                .build();


        System.out.println("🎬 박스오피스 영화 정보 ==============");
        System.out.println("✅ 제목: " + a.getTitle());
        System.out.println("🆔 TMDB ID: " + a.getTmdbMovieId());
        System.out.println("🎞️ KOBIS 영화 코드: " + a.getKobisMovieCd());
        System.out.println("📆 개봉일: " + a.getReleaseDate());
        System.out.println("🕒 러닝타임: " + a.getRuntime() + "분");
        System.out.println("🎭 장르: " + a.getGenres());
        System.out.println("👨‍🎬 감독: " + a.getDirector());
        System.out.println("🖼️ 포스터: " + a.getPosterImage());
        System.out.println("📝 줄거리: " + a.getOverview());
        System.out.println("박스오피스 순위 : " + a.getBoxOfficeRank());
        System.out.println("=================================");


        return a;
    }

    /**
     * ✅ KOBIS 영화 정보에 있는 movieNmEn 속성과 TMDB API를 이용해 영화 정보 가져오기
     */
    private Map<String, Object> fetchTmdbMovieInfo(String movieNm, String movieNmEn, String releaseDate) {

        // 🔧 movieNmEn이 null 또는 공백일 경우 movieNm 사용
        String query = (movieNmEn == null || movieNmEn.trim().isEmpty()) ? movieNm : movieNmEn;

        String searchUrl = TMDB_SEARCH_URL + TMDB_API_KEY + "&query=" + query + "&year="
                + (releaseDate.isEmpty() ? "" : releaseDate.substring(0, 4))
                + "&language=ko-KR"
                + "&append_to_response=credits";

        // System.out.println(searchUrl);

        ResponseEntity<Map> response = restTemplate.getForEntity(searchUrl, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("results")) {
            return Map.of();
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

        if (results.isEmpty()) {
            return Map.of();
        }

        // ✅ `origin_title`이 `movieNmEn`과 일치하는 영화 찾기
        Map<String, Object> matchedMovie = results.stream()
                .filter(movie -> movieNm.equalsIgnoreCase((String) movie.getOrDefault("original_title", "")))  // 🔥 정확한 영화 찾기
                .findFirst()
                .orElse(results.get(0));  // 🎯 없으면 첫 번째 결과 사용

        Integer movieId = (Integer) matchedMovie.get("id");

        // ✅ TMDB API에서 추가 정보 (runtime) 가져오기
        Integer runtime = fetchTmdbMovieRuntime(movieId);

//        return Map.of(
//                "id", matchedMovie.get("id"),
//                "poster_path", "https://image.tmdb.org/t/p/w500" + matchedMovie.get("poster_path"),
//                "overview", matchedMovie.get("overview"),
//                "runtime", runtime   // ✅ 상영 시간 추가
//
//        );

        String director = fetchTmdbMovieDirector(movieId);
        // System.out.println("director : " + director);

        /**
         *  ✅ 상영 시간 추가
         *  ✅ 감독 이름 추가
         */
        return Map.of(
                "id", matchedMovie.get("id"),
                "poster_path", "https://image.tmdb.org/t/p/w500" + matchedMovie.get("poster_path"),
                "overview", matchedMovie.get("overview"),
                "release_date", matchedMovie.get("release_date"),
                "runtime", runtime,
                "director", director
        );

    }

    /**
     * ✅ TMDB API에서 영화 ID를 기반으로 상영 시간 가져오기
     */
    private Integer fetchTmdbMovieRuntime(Integer movieId) {
        if (movieId == null) return null;

        String movieDetailUrl = "https://api.themoviedb.org/3/movie/"
                + movieId + "?api_key="
                + TMDB_API_KEY
                + "&language=ko-KR";

        // System.out.println(movieDetailUrl);

        ResponseEntity<Map> response = restTemplate.getForEntity(movieDetailUrl, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("runtime")) {
            return null;
        }

        return (Integer) response.getBody().get("runtime");
    }

    private String fetchTmdbMovieDirector(Integer movieId) {
        if (movieId == null) return "";

        String movieDetailUrl = "https://api.themoviedb.org/3/movie/"
                + movieId + "?api_key="
                + TMDB_API_KEY
                + "&language=ko-KR&append_to_response=credits";

        // System.out.println("🔍 TMDB 감독 검색 URL: " + movieDetailUrl);

        ResponseEntity<Map> response = restTemplate.getForEntity(movieDetailUrl, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("credits")) {
            System.out.println("❌ credits 정보 없음");
            return "";
        }

        Map<String, Object> credits = (Map<String, Object>) response.getBody().get("credits");
        List<Map<String, Object>> crewList = (List<Map<String, Object>>) credits.get("crew");

        if (crewList == null) {
            System.out.println("❌ crew 정보 없음");
            return "";
        }

        return crewList.stream()
                .filter(crew -> "Director".equalsIgnoreCase((String) crew.get("job")))
                .map(crew -> (String) crew.getOrDefault("name", ""))
                .findFirst()
                .orElse("");
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
        boolean exists = movieRepository.existsByTmdbMovieId(movie.getTmdbMovieId());
        if (exists) {
            System.out.println("⚠️ 이미 저장된 영화: " + movie.getTitle() + " (" + movie.getReleaseDate() + ")");
        }
        return exists;
    }
}