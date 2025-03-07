package com.springstudy.backend.API.Recommand.Service;

import com.springstudy.backend.API.Recommand.Model.Request.RecommandRequest;
import com.springstudy.backend.API.Recommand.Model.Response.RecommandResponse;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommandService {
    private final RestTemplate restTemplate;
    private final HttpEntity<Map> httpEntity;
    public RecommandResponse recommandMovie(RecommandRequest request) {
        // 영화 추천 기능
        // 1. 추천할 영화 id로 추천 영화를 검색함. sample = 20
        // 2. 5개의 영화를 추천 엔티티에 저장함.
        String url = "https://api.themoviedb.org/3/movie/"+request.movie_id()+"/similar?language=en-US&page=1";
        ResponseEntity<Map> response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

        List<String> titles = new ArrayList<>();
        if (results != null) {
            for (Map<String, Object> result : results) {
                if (result.containsKey("title")) {
                    titles.add((String) result.get("title"));
                }
            }
        }
        System.out.println(titles);

        return new RecommandResponse(ErrorCode.SUCCESS, new Movie());
    }
}
