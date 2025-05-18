package com.springstudy.backend.API.AI.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.AI.Model.AIRecommendedMovieDTO;
import com.springstudy.backend.API.AI.Model.AIRequest;
import com.springstudy.backend.API.AI.Model.AIResponse;
import com.springstudy.backend.API.AI.Model.AIUserResponseDTO;
import com.springstudy.backend.API.Repository.AIRepository;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.MovieRepository;
import com.springstudy.backend.API.Repository.ReviewRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIRepository aiRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Qualifier("gptRestTemplate")
    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private String model = "GPT-4.5";
    private String url = "https://api.openai.com/v1/chat/completions";

    @Value(" ${api.GPT_API_KEY}")
    private String apikey;

    private final ObjectMapper objectMapper;

    private String getLikeMovies(Long id){
        List<Review> reviewList = reviewRepository.findByUserId(id);
        if(reviewList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        //if(reviewList.isEmpty() || reviewList.size() < 5){throw new CustomException(ErrorCode.NOT_EXIST_USER);}

        String likeMovies = "";
        for(Review review : reviewList){
            if(review.getRating() >= 4.0){
                Movie m = review.getMovie();
                likeMovies = likeMovies + m.getTitle()+", ";
            }
        }
        return likeMovies;
    }
    private String getBoxoffice(Long id){

        List<Movie> movieList = movieRepository.findAll();
        if(movieList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        String movieData = "";
        for(int i= 0;i<movieList.size();i++){
            Movie m = movieList.get(i);
            if(reviewRepository.findByMovieIdAndUserId(m.getId(), id).isPresent()){continue;}
            movieData = movieData + "영화: "+ m.getTitle()+", 장르: "+m.getGenres()+" 줄거리: "+m.getOverview()+"\n";
        }
        return movieData;
    }

    private HttpEntity<Map<String, Object>> makeHttpEntity(String likeMovies, String movieData){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        // messages 리스트를 올바르게 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여"+ movieData +"에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));
        messages.add(Map.of("role", "user", "content", likeMovies+"를 재밌게 봤는데 비슷한 영화 추천해줘."));

        requestBody.put("messages", messages);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);
        return new HttpEntity<Map<String, Object>>(requestBody, headers);
    }

    private String[] responseToString(ResponseEntity<String> gptResponse){
        String[] content = null;
        try{
            JsonNode rootNode = objectMapper.readTree(gptResponse.getBody());
            content = rootNode.path("choices").get(0).path("message").path("content").asText().split(":");

            if(content.length == 0)throw new CustomException(ErrorCode.GPT_PATH_ERROR);
            for(int i= 0;i<content.length;i++) {System.out.println(content[i]);}
        }
        catch(JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PATH_ERROR);
        }
        return content;
    }

    private AI saveResponse(User user, String title, String reason){
        Optional<Movie> recommandMovieOptional = movieRepository.findByTitle(title);
        if(recommandMovieOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}
        System.out.println("movieid: "+recommandMovieOptional.get().getId()+" reason: "+reason +" id:" +user);
        AI ai = AI.builder()
                .user(user)
                .movieId(recommandMovieOptional.get().getId())
                .reason(reason)
                .build();
        return aiRepository.save(ai);
    }

    public AIResponse synopsis(AIRequest aiRequest){

        Long id = aiRequest.user_id();
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        User user = userOptional.get();
        // 1. 사용자 조회

        String likeMovies = getLikeMovies(id);
        System.out.println("likemovies: "+likeMovies);
        // 2. 사용자 4.0 이상 리뷰 영화 조회.

        String movieData = getBoxoffice(id);
        System.out.println("\nmoviedata: "+movieData);
        // 3. 박스오피스 영화 조회.

        HttpEntity<Map<String, Object>> requestEntity = makeHttpEntity(likeMovies, movieData);
        ResponseEntity<String> gptResponse = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println(gptResponse.getBody());
        // 4. 영화 내용 + api key로 요청 데이터 만들고 gpt 통신.

        String[] content= responseToString(gptResponse);
        // 5. 통신 결과를 string으로 맵핑.

        String title = content[1].split("\n")[0].trim();
        String reason = content[2].trim();
        AI result = saveResponse(user, title, reason);
        System.out.println(result.toString());
        // 6. 추천 결과를 저장.
        if(result == null){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        return new AIResponse(ErrorCode.SUCCESS, result.getMovieId(), result.getReason());
    }

    //----------------------------------------------------------------------------

    /*
    사용자가 작성한 리뷰가 없어도 작동하도록 설정
     */
    public AIResponse synopsisV2(Long userId){

        Long id = userId;
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        User user = userOptional.get();
        // 1. 사용자 조회

        String likeMovies = getLikeMoviesV2(id);
        System.out.println("likemovies: "+likeMovies);
        // 2. 사용자 4.0 이상 리뷰 영화 조회.

        /*
        사용자가 본 영화 중에서 랜덤으로 비슷한 영화 추천?
         */

        String movieData = getBoxofficeV2(id);
        System.out.println("\nmoviedata: "+movieData);
        // 3. 박스오피스 영화 조회.

        HttpEntity<Map<String, Object>> requestEntity = makeHttpEntityV2(likeMovies, movieData);
        ResponseEntity<String> gptResponse = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println(gptResponse.getBody());
        // 4. 영화 내용 + api key로 요청 데이터 만들고 gpt 통신.

        String[] content= responseToString(gptResponse);
        // 5. 통신 결과를 string으로 맵핑.

        String title = content[1].split("\n")[0].trim();
        String reason = content[2].trim();
        AI result = saveResponseV2(user, title, reason);
        System.out.println(result.toString());
        // 6. 추천 결과를 저장.
        if(result == null){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        return new AIResponse(ErrorCode.SUCCESS, result.getMovieId(), result.getReason());
    }

    private HttpEntity<Map<String, Object>> makeHttpEntityV2(String likeMovies, String movieData){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        // messages 리스트를 올바르게 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여 "+ movieData +"중에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));

        if (likeMovies != null && !likeMovies.isEmpty()) {
            messages.add(Map.of("role", "user", "content", likeMovies+"를 재밌게 봤는데 " + movieData + "중에서 비슷한 영화 추천해줘."));
        }


        requestBody.put("messages", messages);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);
        return new HttpEntity<Map<String, Object>>(requestBody, headers);
    }

    private String getLikeMoviesV2(Long id){
        List<Review> reviewList = reviewRepository.findByUserId(id);

        // if(reviewList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        //if(reviewList.isEmpty() || reviewList.size() < 5){throw new CustomException(ErrorCode.NOT_EXIST_USER);}

        if(reviewList.isEmpty()){
            return "";
        }

        String likeMovies = "";
        for(Review review : reviewList){
            if(review.getRating() >= 4.0){
                Movie m = review.getMovie();
                likeMovies = likeMovies + m.getTitle()+", ";
            }
        }
        return likeMovies;
    }


    private String getBoxofficeV2(Long id){

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Movie> movieList = movieRepository.findAllByFetchedDate(yesterday);

        if(movieList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        String movieData = "";
        for(int i= 0;i<movieList.size();i++){
            Movie m = movieList.get(i);
            if(reviewRepository.findByMovieIdAndUserId(m.getId(), id).isPresent()){continue;}
            movieData = movieData + "영화: "+ m.getTitle()+", 장르: "+m.getGenres()+" 줄거리: "+m.getOverview()+"\n";
            System.out.println(movieData);
        }
        return movieData;
    }


    private AI saveResponseV2(User user, String title, String reason){
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MOVIE));

        Long movieId = movie.getId();

        // user와 movieId가 모두 같은 AI 엔티티가 존재하는지 확인
        Optional<AI> existingAI = aiRepository.findByUserAndMovieId(user, movieId);

        if (existingAI.isPresent()) {
            AI ai = existingAI.get();
            ai = AI.builder()
                    .id(ai.getId())  // 기존 ID 유지
                    .user(user)
                    .movieId(movieId)
                    .reason(reason) // ✅ reason만 갱신
                    .build();
            return aiRepository.save(ai); // save는 ID가 같으면 update 동작
        }

        // 존재하지 않는 경우 새로 저장
        AI ai = AI.builder()
                .user(user)
                .movieId(movieId)
                .reason(reason)
                .build();
        return aiRepository.save(ai);
    }

    /*
    public List<AI> getAllAIs() {
        return aiRepository.findAll();
    }

     */

    public List<AIUserResponseDTO> getAllAIs() {
        return aiRepository.findAll().stream()
                .map(ai -> new AIUserResponseDTO(
                        ai.getId(),
                        ai.getMovieId(),
                        ai.getReason(),
                        ai.getUser().getId(),
                        ai.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    public void deleteAIById(Long id) {
        if (!aiRepository.existsById(id)) {
            throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);  // 예외처리 선택사항
        }
        aiRepository.deleteById(id);
    }

    //----------------------------------------------------------------------------

    public List<AIRecommendedMovieDTO> getAIRecommendedMovies(User user) {
        List<AI> aiList = aiRepository.findByUser(user);

        // movieId -> reason 매핑
        Map<Long, String> reasonMap = aiList.stream()
                .collect(Collectors.toMap(AI::getMovieId, AI::getReason));

        List<Movie> movies = movieRepository.findAllById(reasonMap.keySet());

        return movies.stream()
                .map(movie -> new AIRecommendedMovieDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getPosterImage() != null ? movie.getPosterImage() : "/placeholder.svg?height=256&width=200&text=" + movie.getTitle(),
                        reasonMap.get(movie.getId())  // ✅ 추천 이유도 전달
                ))
                .collect(Collectors.toList());
    }
}
