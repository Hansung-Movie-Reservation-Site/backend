package com.springstudy.backend.API.AI.Controller;

import com.springstudy.backend.API.AI.Model.AIRequest;
import com.springstudy.backend.API.AI.Model.AIResponse;
import com.springstudy.backend.API.AI.Service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/AIRecommand")
@RequiredArgsConstructor
public class AIcontroller {
    private final AIService aiService;

    @PostMapping("/synopsis")
    public AIResponse synopsis(@RequestBody AIRequest aiRequest) {
        System.out.println(aiRequest.user_id());
        return aiService.synopsis(aiRequest);
    }
}
