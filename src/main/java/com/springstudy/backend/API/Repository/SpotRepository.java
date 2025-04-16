package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    // ✅ Spot name으로 Spot 엔티티 조회
    Optional<Spot> findByName(String name);


}