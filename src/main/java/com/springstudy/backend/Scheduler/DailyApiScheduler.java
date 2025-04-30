package com.springstudy.backend.Scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class DailyApiScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String MOVIE_API_URL =
            "http://localhost:8080/api/v1/movies/daily";
    private static final LocalTime SCHEDULED_TIME = LocalTime.of(9, 0); // 오전 9시

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
