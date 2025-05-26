package com.springstudy.backend.Scheduler;

import com.springstudy.backend.API.Screening.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매 5분마다 체크
    @Scheduled(fixedRate = 10 * 1000)
    public void checkAndNotify() {
        // System.out.println("동작 중 입니다.");

        notificationService.notifyUpcomingScreeningsV2();
        // notificationService.cancelExpiredPendingOrders();

    }
}
