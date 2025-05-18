package com.springstudy.backend.API.AI.Controller;

import com.springstudy.backend.API.AI.Model.AIRecommendedMovieDTO;
import com.springstudy.backend.API.AI.Model.AIRequest;
import com.springstudy.backend.API.AI.Model.AIResponse;
import com.springstudy.backend.API.AI.Model.AIUserResponseDTO;
import com.springstudy.backend.API.AI.Service.AIService;
import com.springstudy.backend.API.Repository.Entity.AI;
import com.springstudy.backend.API.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springstudy.backend.API.Repository.Entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/AIRecommand")
@RequiredArgsConstructor
public class AIcontroller {
    private final AIService aiService;
    private final UserRepository userRepository;

    @PostMapping("/synopsis")
    public AIResponse synopsis(@RequestBody AIRequest aiRequest) {
        System.out.println(aiRequest.user_id());
        return aiService.synopsis(aiRequest);
    }

    @GetMapping("/recommended")
    public List<AIRecommendedMovieDTO> getRecommendedMovies(@RequestParam("userId") Long userId) {

        // ✅ userId 기반으로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        return aiService.getAIRecommendedMovies(user);
    }

    //----------------------------------------------------------------

    @PostMapping("/synopsisV2")
    public AIResponse synopsisV2(@RequestParam("userId") Long userId) {
        System.out.println(userId);
        return aiService.synopsisV2(userId);
    }

    @GetMapping("/getAll")
    public List<AIUserResponseDTO> getAllAIs() {
        return aiService.getAllAIs();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAI(@PathVariable Long id) {
        aiService.deleteAIById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    //----------------------------------------------------------------

}
