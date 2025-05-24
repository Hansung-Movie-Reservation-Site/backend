package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.AI;
import com.springstudy.backend.API.Repository.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Optional;

@Repository
public interface AIRepository extends JpaRepository<AI , Long> {
    Optional<List<AI>> findByUserId(Long user_id);

    List<AI> findByUser(User user);  // ✅ 유저별 AI 추천 데이터 가져오기

    Optional<AI> findByUserAndMovieId(User user, Long movieId);

    List<AI> findAllByUserId(Long userId);
}
