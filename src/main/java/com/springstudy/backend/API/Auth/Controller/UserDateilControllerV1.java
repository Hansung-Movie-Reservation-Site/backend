package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.Request.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Service.ChangeDetailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/detail")
@RequiredArgsConstructor
public class UserDateilControllerV1 {
    private final ChangeDetailService changeDetailService;
    @PostMapping("/change")
    public ChangeDetailResponse changeDetail(
            @RequestBody ChangeDetailRequest changeDetailRequest
    ){
        return changeDetailService.changeDetail(changeDetailRequest);
    }
}
