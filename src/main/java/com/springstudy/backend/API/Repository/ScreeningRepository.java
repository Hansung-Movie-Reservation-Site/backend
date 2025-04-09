package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Room;
import com.springstudy.backend.API.Repository.Entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    // ✅ roomid 목록과 date가 일치하는 Screening 데이터 조회
    List<Screening> findByRoomIdInAndDate(List<Long> roomIds, LocalDate date);

    // ✅ roomId, date, movieId가 일치하는 Screening 데이터 조회
    List<Screening> findByRoomIdInAndDateAndMovieId(List<Long> roomIds, LocalDate date, Long movieId);

    // ✅ 중복 제거 후 모든 영화 정보 조회
    @Query("SELECT DISTINCT s.movie FROM Screening s")
    List<Movie> findAllMoviesFromScreenings();

    // ✅ 특정 문자열을 포함하는 영화 제목의 상영 정보 조회
    @Query("SELECT s FROM Screening s WHERE LOWER(s.movie.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Screening> findByMovieTitleContaining(@Param("title") String title);

    List<Screening> findByRoomAndDate(Room room, LocalDate date);

    List<Screening> findByRoomIdAndDate(Long roomId, LocalDate date);

    boolean existsByMovieAndRoomAndDateAndStartAndFinishAndPrice(
            Movie movie,
            Room room,
            LocalDate date,
            LocalTime start,
            LocalTime finish,
            int price
    );
}