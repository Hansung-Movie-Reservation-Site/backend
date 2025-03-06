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

@Service
@RequiredArgsConstructor
public class RecommandService {
    private final RestTemplate restTemplate;
    private final HttpEntity<String> httpEntity;
    public RecommandResponse recommandMovie(RecommandRequest request) {
        // 영화 추천 기능
        // 1. 추천할 영화 id로 추천 영화를 검색함. sample = 20
        // 2. 5개의 영화를 추천 엔티티에 저장함.
        String url = "https://api.themoviedb.org/3/movie/"+request.movie_id()+"/similar?language=ko-KR&page=1";
        ResponseEntity response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println(response.getBody());
        return new RecommandResponse(ErrorCode.SUCCESS, new Movie());
    }
}
