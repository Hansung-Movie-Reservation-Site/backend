package com.springstudy.backend.Scheduler;

import com.springstudy.backend.API.Movie.Service.MovieService;
import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Room;
import com.springstudy.backend.API.Room.Service.RoomService;
import com.springstudy.backend.API.Screening.Response.ScreeningAddDTO;
import com.springstudy.backend.API.Screening.Service.ScreeningService;
import com.springstudy.backend.API.Spot.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DailyApiScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String MOVIE_API_URL =
            "http://localhost:8080/api/v1/movies/daily";
    private static final LocalTime SCHEDULED_TIME = LocalTime.of(0, 0); // 오전 9시

    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final RoomService roomService;
    private final SpotService spotService;

    /*
    // ✅ 매일 09:00에 두 API 호출
    @Scheduled(cron = "0 0 9 * * *")
    public void scheduledDailyApiCalls() {
        System.out.println("[스케줄러] 정해진 시간에 API 호출 시작");

        callMovieApi();
        callScreeningApi();

        System.out.println("[스케줄러] 정해진 시간에 API 호출 완료");
    }

    // ✅ 앱 시작 시 현재 시간이 09:00 이후면 즉시 두 API 호출
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartupIfNeeded() {
        if (LocalTime.now().isAfter(SCHEDULED_TIME)) {
            System.out.println("[스케줄러] 애플리케이션이 09:00 이후에 실행됨 → 즉시 API 호출");

            callMovieApi();
            callScreeningApi();
        }
    }

     */

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledDailyApiCalls() {
        System.out.println("[스케줄러] 00:00에 자동 실행 시작");
        callDailyApisWeek();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartupIfNeeded() {
        if (LocalTime.now().isAfter(SCHEDULED_TIME)) {
            System.out.println("[스케줄러] 애플리케이션 시작 시 00:00 이후 → 즉시 실행");
            callDailyApisWeek();
        }
    }

    public void callDailyApisWeek() {
        try {
            // ✅ 0. 누락된 Spot 데이터 삽입
            spotService.insertMissingSpots();
            System.out.println("[✅] Spot 누락된 데이터 삽입 완료");

            // ✅ 1. Room + Seat 생성
            int roomsToCreatePerSpot = 5;
            List<Room> createdRooms = roomService.createAllRoomsWithSeatsV2(roomsToCreatePerSpot);
            System.out.printf("[✅] 상영관 및 좌석 생성 완료 (%d개 Room 생성)\n", createdRooms.size());

            List<Movie> movies = movieService.fetchAndSaveDailyBoxOfficeMovies();
            System.out.println("[✅] 일간 박스오피스 영화 저장 완료");

            int screeningsPerRoom = 3;
            int price = 10000;

            List<Long> movieIds = movies.stream()
                    .map(Movie::getId)
                    .collect(Collectors.toList());

            List<ScreeningAddDTO> createdScreenings =
                    screeningService.createScreeningsForWeekWithSpecificMovies(screeningsPerRoom, price, movieIds);

            System.out.printf("[✅] 일주일간 상영 일정 생성 완료 (%d건)\n", createdScreenings.size());
        } catch (Exception e) {
            System.err.println("[❌] 일간 API 처리 실패: " + e.getMessage());
        }
    }


    public void callDailyApis() {
        try {
            // 1️⃣ 어제자 박스오피스 영화 가져오기 및 저장
            List<Movie> movies = movieService.fetchAndSaveDailyBoxOfficeMovies();
            System.out.println("[✅] 일간 박스오피스 영화 저장 완료");

            // 2️⃣ 오늘 날짜 기준 상영 일정 생성
            LocalDate today = LocalDate.now();
            int screeningsPerRoom = 3;
            int price = 10000;

            List<Long> movieIds = movies.stream()
                    .map(Movie::getId)
                    .collect(Collectors.toList());

            List<ScreeningAddDTO> createdScreenings =
                    screeningService.createScreeningsForWeekWithSpecificMovies(screeningsPerRoom, price, movieIds);

            System.out.printf("[✅] 상영 일정 생성 완료 (%d건)\n", createdScreenings.size());
        } catch (Exception e) {
            System.err.println("[❌] 일간 API 처리 실패: " + e.getMessage());
        }
    }



    private void callMovieApi() {
        try {
            restTemplate.getForObject(MOVIE_API_URL, String.class);
            System.out.println("[API 호출] 영화 API 호출 성공");
        } catch (Exception e) {
            System.err.println("[API 호출 실패] 영화 API: " + e.getMessage());
        }
    }

    private void callScreeningApi() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String url = String.format("http://localhost:8080/api/v1/screening/generate?date=%s&count=3&price=10000", today);

            // ✅ GET → POST로 수정
            restTemplate.postForObject(url, null, String.class);

            System.out.println("[API 호출] 상영 일정 API 호출 성공");
        } catch (Exception e) {
            System.err.println("[API 호출 실패] 상영 일정 API: " + e.getMessage());
        }
    }
}
