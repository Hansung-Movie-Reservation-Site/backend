package com.springstudy.backend.API.AI.Controller;

import com.springstudy.backend.API.AI.Model.*;
import com.springstudy.backend.API.AI.Service.AIService;
import com.springstudy.backend.API.Repository.Entity.AI;
import com.springstudy.backend.API.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springstudy.backend.API.Repository.Entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/AIRecommand")
@RequiredArgsConstructor
public class AIcontroller {
    private final AIService aiService;
    private final UserRepository userRepository;

    @PostMapping("/synopsis")
    public AIResponseV2 synopsis(@RequestBody AIRequest aiRequest) {
        System.out.println(aiRequest.user_id());
        return aiService.synopsis(aiRequest);
    }









































    //----------------------------------------------------------------

    @GetMapping("/recommended")
    public List<AIRecommendedMovieDTO> getRecommendedMovies(@RequestParam("userId") Long userId) {


        /*
        // ✅ userId 기반으로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));



        return aiService.getAIRecommendedMovies(user);

         */

        // ✅ userId 기반으로 User 엔티티 조회
        Optional<User> optionalUser = userRepository.findById(userId);

        // ✅ 존재하지 않으면 빈 배열 반환
        if (optionalUser.isEmpty()) {
            return Collections.emptyList();
        }

        // ✅ 존재하면 추천 결과 반환
        User user = optionalUser.get();
        return aiService.getAIRecommendedMovies(user);
    }

    @PostMapping("/synopsisV2")
    public AIResponse synopsisV2(@RequestParam("userId") Long userId,
                                 @RequestParam("type") String type) {
        // System.out.println(userId);
        return aiService.synopsisV2(userId, type);
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


    @GetMapping("/getByUser")
    public ResponseEntity<List<AIResponseDTO>> getAIRecommendationsByUser(@RequestParam("userId") Long userId) {
        List<AIResponseDTO> result = aiService.getAIRecommendationsByUserId(userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deleteByUser")
    public ResponseEntity<String> deleteAIRecommendationsByUserId(@RequestParam("userId") Long userId) {
        aiService.deleteAllAIByUserId(userId);
        return ResponseEntity.ok("✅ userId " + userId + "에 해당하는 추천 데이터를 삭제했습니다.");
    }

    //----------------------------------------------------------------

}
