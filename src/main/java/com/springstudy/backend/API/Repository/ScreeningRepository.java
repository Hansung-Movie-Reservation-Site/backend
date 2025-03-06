package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    // ✅ roomid 목록과 date가 일치하는 Screening 데이터 조회
    List<Screening> findByRoomIdInAndDate(List<Long> roomIds, LocalDate date);

    // ✅ roomId, date, movieId가 일치하는 Screening 데이터 조회
    List<Screening> findByRoomIdInAndDateAndMovieId(List<Long> roomIds, LocalDate date, Long movieId);
}