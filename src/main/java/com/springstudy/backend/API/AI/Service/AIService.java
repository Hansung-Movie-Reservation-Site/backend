package com.springstudy.backend.API.AI.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.AI.Model.AIRequest;
import com.springstudy.backend.API.AI.Model.AIResponse;
import com.springstudy.backend.API.AI.Model.GptRequest;
import com.springstudy.backend.API.AI.Model.GptResponse;
import com.springstudy.backend.API.Repository.AIRepository;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.MovieRepository;
import com.springstudy.backend.API.Repository.ReviewRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

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
    public AIResponse synopsis(AIRequest aiRequest){

        Long id = aiRequest.user_id();
        Optional<User> userOptional = userRepository.findById(aiRequest.user_id());
        if(userOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}

        List<Review> reviewList = reviewRepository.findByUserId(aiRequest.user_id());
        if(reviewList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}

        String LikeMovies = "";
        //if(reviewList.size() < 5){throw new CustomException(ErrorCode.NOT_EXIST_USER);}

        for(Review review : reviewList){
            if(review.getRating() > 4.0){
                Movie m = review.getMovie();
                LikeMovies = LikeMovies + m.getTitle()+", ";
            }
        }
        System.out.println(LikeMovies);

        List<Movie> movieList = movieRepository.findAll();
        if(movieList.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}
        String movieData = "";
        for(int i= 0;i<movieList.size();i++){
            Movie m = movieList.get(i);
            if(reviewRepository.findByMovieIdAndUserId(m.getId(), id).isPresent()){continue;}
            movieData = movieData + "영화: "+ m.getTitle()+", 장르: "+m.getGenres()+" 줄거리: "+m.getOverview()+"\n";
        }
        System.out.println(movieData);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        // messages 리스트를 올바르게 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "사용자가 본 영화를 분석하여"+ movieData +"에서 사용자가 가장 선호할 것으로 예측되는 영화를 추천합니다. ex) 추천 영화가 아이리시맨이라면 추천영화:아이리시맨\n추천이유:인간이 범죄에 가담하면서 자아가 타락되는 이야기를 선호."));
        messages.add(Map.of("role", "user", "content", LikeMovies+"를 재밌게 봤는데 비슷한 영화 추천해줘."));

        requestBody.put("messages", messages);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        GptRequest gptRequest = new GptRequest(model, "어벤져스가 뭐야.");
        ResponseEntity<String> gptResponse = restTemplate.postForEntity(url, requestEntity, String.class);

        System.out.println(LikeMovies);
        System.out.println(gptResponse.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode rootNode = objectMapper.readTree(gptResponse.getBody());
            String[] content = rootNode.path("choices").get(0).path("message").path("content").asText().split(":");

            for(int i= 0;i<content.length;i++) {
                System.out.println(content[i]);
            }
            System.out.println(content[1].trim()+"\n");
            Optional<Movie> recommandMovieOptional = movieRepository.findByTitle(content[1].split("\n")[0].trim());
            if(recommandMovieOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}
            AI ai = AI.builder()
                    .movieId(recommandMovieOptional.get().getId())
                    .reason(content[2].trim())
                    .build();
            AI result = aiRepository.save(ai);
            if(result == null){throw new CustomException(ErrorCode.NOT_EXIST_MOVIE);}
        }
        catch(JsonMappingException e){

        }
        catch(JsonProcessingException e){

        }

        return new AIResponse(ErrorCode.SUCCESS, 134, "추천 이유");
    }
}
