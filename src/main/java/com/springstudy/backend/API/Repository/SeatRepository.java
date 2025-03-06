package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    // ✅ 특정 Room ID와 일치하는 Seat 정보 조회
    List<Seat> findByRoomId(Long roomId);

    // ✅ 특정 ID 리스트와 일치하는 Seat 목록 조회
    List<Seat> findByIdIn(List<Long> seatIds);
}