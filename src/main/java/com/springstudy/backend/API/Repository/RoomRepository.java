package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // ✅ Spot ID로 Room 목록 조회
    List<Room> findBySpotId(Long spotId);
}