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
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여"+ movieData +"에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화^^아이리시맨\n추천이유^^인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));
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
            content = rootNode.path("choices").get(0).path("message").path("content").asText().split("\n");

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
        // 5. 통신 결과를 string으로 맵핑. 2 3

        String title = content[0].split("\\^\\^")[1].trim();
        System.out.println("content 최종 title: "+title);
//        String r = content[1].split("\\^\\^")[1].trim();
//        System.out.println("content 최종 r: "+r);
        String reason;
        if(!content[1].equals(""))reason = content[1].split("\\^\\^")[1].trim();
        else reason = content[2].split("\\^\\^")[1].trim();
        System.out.println("content 최종 reason: "+reason);
        AI result = saveResponse(user, title, reason);

        System.out.println(result.toString());
        // 6. 추천 결과를 저장.
        if(result == null){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        return new AIResponse(ErrorCode.SUCCESS, result.getMovieId(), result.getReason());
    }



















































    // ---------------------------------------------------------------------

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

    private String[] responseToStringV2(ResponseEntity<String> gptResponse){
        String[] content = null;
        try{
            JsonNode rootNode = objectMapper.readTree(gptResponse.getBody());
            // content = rootNode.path("choices").get(0).path("message").path("content").asText().split("\n");
            content = rootNode.path("choices").get(0).path("message").path("content").asText().split(":");

            if(content.length == 0)throw new CustomException(ErrorCode.GPT_PATH_ERROR);
            for(int i= 0;i<content.length;i++) {System.out.println(content[i]);}
        }
        catch(JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PATH_ERROR);
        }
        return content;
    }


    public AIResponse synopsisV2(Long userId, String type){

        Long id = userId;
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        User user = userOptional.get();
        // 1. 사용자 조회

        String likeMovies = getLikeMoviesV2(id);
        System.out.println("likemovies: "  + likeMovies);

        System.out.println("--------------------------------------------\n");
        // 2. 사용자 4.0 이상 리뷰 영화 조회.

        String movieData = getBoxofficeV2(id);
        System.out.println("\nmoviedata: " + movieData);
        // 3. 박스오피스 영화 조회.

        HttpEntity<Map<String, Object>> requestEntity = makeHttpEntityV2(likeMovies, movieData, type);
        ResponseEntity<String> gptResponse = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println(gptResponse.getBody());
        // 4. 영화 내용 + api key로 요청 데이터 만들고 gpt 통신.

        // String[] content= responseToStringV2(gptResponse);
        // 5. 통신 결과를 string으로 맵핑. 2 3



        // ✅ 1. JSON에서 content 추출
        JSONObject obj = new JSONObject(gptResponse.getBody());
        JSONArray choices = obj.getJSONArray("choices");
        String content = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        System.out.println("content: " + content);
        System.out.println("----------------------------------------");

        // ✅ 2. 추천 영화 추출
        // String movieTitle = extractField(content, "추천 영화[:：]\\s*(.+)");
        // String movieReason = extractField(content, "추천 이유[:：]\\s*(.+)");

        String movieTitle = extractMovieTitle(content);
        String movieReason = extractMovieReason(content);

        System.out.println("🎬 추천 영화: " + movieTitle);
        System.out.println("💡 추천 이유: " + movieReason);

        // AI result = saveResponseV2(user, title, reason);
        AI result = saveResponseV2(user, movieTitle, movieReason);


        // 6. 추천 결과를 저장.
        if(result == null){throw new CustomException(ErrorCode.NOT_EXIST_SPOT);}

        return new AIResponse(ErrorCode.SUCCESS, result.getMovieId(), result.getReason());
    }

    private static String extractMovieTitle(String content) {
        Pattern pattern = Pattern.compile("추천 영화[:：]\\s*(.*?)\\s*추천 이유[:：]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "없음";
    }

    private static String extractMovieReason(String content) {
        Pattern pattern = Pattern.compile("추천 이유[:：]\\s*(.+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "없음";
    }

    private HttpEntity<Map<String, Object>> makeHttpEntityV2(String likeMovies, String movieData, String type){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        // messages 리스트를 올바르게 구성
        List<Map<String, String>> messages = new ArrayList<>();
        // messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여 "+ movieData +"중에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화^^아이리시맨\n추천이유^^인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));
        // messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여"+ movieData +"에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));


        // messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여"+ movieData +"에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));

        // 리뷰 기반 추천
        if (type.equals("review")) {
            if (likeMovies != null && !likeMovies.isEmpty()) {
                System.out.println("review 기반, 작성된 리뷰 존재");
                messages.add(Map.of("role", "user", "content", likeMovies+"를 재밌게 봤는데 "
                        + movieData + "중에서 비슷한 영화를 추천하고 그 이유를 알려줘. "
                        + "이 때 반드시 영화를 추천하고 추천 영화, 추천 이유를 작성해줘. "
                        + "그리고 영화 제목은 훼손하지 말고 그대로 보존해줘. "
                        + "ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:이러한 점이 비슷해서 추천."));
            }
            else {
                System.out.println("review 기반, 작성된 리뷰 X");
                messages.add(Map.of("role", "user", "content", movieData + "중에서 아무거나 영화를 추천해줘. "
                        + "이 때 반드시 영화를 추천하고 추천 영화, 추천 이유를 작성해줘. "
                        + "그리고 영화 제목은 훼손하지 말고 그대로 보존해줘. "
                        + "ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인기가 많고 사용자들에게 평가가 좋기 때문."));
            }
        }

        // 장르 기반 추천
        else {
            System.out.println("장르 기반");
            messages.add(Map.of("role", "user", "content", movieData + "중에서"
                    + type + "와 가장 비슷한 장르의 영화를 추천하고 그 이유를 알려줘. "
                    + "이 때 반드시 영화를 추천하고 추천 영화, 추천 이유를 작성해줘. "
                    + "그리고 영화 제목은 훼손하지 말고 그대로 보존해줘. "
                    + "ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:이러한 점에서 장르가 비슷하다고 판단하여 추천."));
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


        LocalDate today = LocalDate.now();
        List<Movie> movieList = movieRepository.findAllByFetchedDate(today);


        // List<Movie> movieList = movieRepository.findAll();

        if(movieList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}

        String movieData = "";
        for(int i= 0;i<movieList.size();i++){
            Movie m = movieList.get(i);
            if(reviewRepository.findByMovieIdAndUserId(m.getId(), id).isPresent()){continue;}
            // if(m.getFetchedDate().isBefore(LocalDate.now())){continue;}
            movieData = movieData + "영화: "+ m.getTitle()+", 장르: "+m.getGenres()+" 줄거리: "+m.getOverview()+"\n";
            // System.out.println(movieData);
        }

        System.out.println(movieData);
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


    // ------------------------------------------------------------------------



}
