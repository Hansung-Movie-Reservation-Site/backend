package com.springstudy.backend.API.Recommand.Controller;

import com.springstudy.backend.API.Recommand.Model.Request.RecommandRequest;
import com.springstudy.backend.API.Recommand.Model.Response.RecommandResponse;
import com.springstudy.backend.API.Recommand.Service.RecommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/recommand")
@RequiredArgsConstructor
public class RecommandController {
    private final RecommandService recommandService;

    @PostMapping("/movie")
    public RecommandResponse recommand(RecommandRequest recommandRequest) {
        return recommandService.recommandMovie(recommandRequest);
    }
}
