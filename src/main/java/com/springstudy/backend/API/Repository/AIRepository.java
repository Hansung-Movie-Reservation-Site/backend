package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.AI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Optional;

@Repository
public interface AIRepository extends JpaRepository<AI , Integer> {
    Optional<List<AI>> findByUserId(Long user_id);
}
