package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // ✅ Spot ID로 Room 목록 조회
    List<Room> findBySpotId(Long spotId);

    @Query("SELECT MAX(r.roomnumber) FROM Room r WHERE r.spot.id = :spotId")
    Optional<Long> findMaxRoomNumberBySpotId(Long spotId);

    int countBySpotId(Long spotId);
}