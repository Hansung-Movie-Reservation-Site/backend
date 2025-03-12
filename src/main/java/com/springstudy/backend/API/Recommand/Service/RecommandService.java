//package com.springstudy.backend.API.Recommand.Service;
//
//import com.springstudy.backend.API.Recommand.Model.Request.RecommandRequest;
//import com.springstudy.backend.API.Recommand.Model.Response.RecommandResponse;
//import com.springstudy.backend.API.Repository.Entity.Movie;
//import com.springstudy.backend.Common.ErrorCode.CustomException;
//import com.springstudy.backend.Common.ErrorCode.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class RecommandService {
//    private final RestTemplate restTemplate;
//
//    @Value("${api.RECOMMEND_API_KEY}")
//    String RECOMMEND_API_KEY;
//
//    public RecommandResponse recommandMovie(RecommandRequest request) {
//        // 영화 추천 기능
//        // 1. 추천할 영화 id로 추천 영화를 검색함. sample = 20
//        // 2. 5개의 영화를 추천 엔티티에 저장함.
//        ResponseEntity<Map> response = null;
//        HttpHeaders httpHeaders = new HttpHeaders();
//        HttpEntity<Map>  httpEntity = new HttpEntity(httpHeaders);
//        httpEntity.getHeaders().set("accept", "application/json");
//        httpEntity.getHeaders().set("Authorization", "Bearer "+RECOMMEND_API_KEY);
//
//        try{
//            String url = "https://api.themoviedb.org/3/movie/"+request.movie_id()+"/similar?language=en-US&page=1";
//            response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
//        }
//        catch(RestClientException e){
//            log.error(e.getMessage());
//            throw new CustomException("서버 응답 오류", ErrorCode.RESTAPI_ERROR);
//        }
//
//        if (response == null || response.getBody() == null) {
//            throw new CustomException("영화 추천 API 응답이 비어 있습니다.", ErrorCode.API_RESPONSE_NULL);
//        }
//        List<Map<String, Object>> recommandResponse = (List<Map<String, Object>>) response.getBody().get("results");
//        List<String> titles = extractTitle(recommandResponse);
//
//        //todo
//        //영화 제목으로 영화 api 요청해서 검색.
//        //검색된 영화를 recommand Movie와 Movie에 저장함.
//
//        System.out.println(titles);
//
//        return new RecommandResponse(ErrorCode.SUCCESS, new Movie());
//    }
//    private List<String> extractTitle(List<Map<String,Object>> recommandResponse){
//        List<String> titles = new ArrayList<>();
//        if(recommandResponse == null){
//            throw new CustomException("영화 추천 API 응답 형식이 맞지 않습니다.",ErrorCode.API_RESPONSE_MISMATCH);
//        }
//        for (Map<String, Object> r : recommandResponse) {
//            if (r.containsKey("title")) {
//                titles.add((String) r.get("title"));
//            }
//        }
//        return titles;
//    }
//}
