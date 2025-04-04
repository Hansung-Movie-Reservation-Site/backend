package com.springstudy.backend.API.Screening.Service;

import com.springstudy.backend.API.Repository.Entity.Order;
import com.springstudy.backend.API.Repository.Entity.Screening;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.OrderRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * User을 조회한 후 Order을 조회하여 트랜잭션 문제 발생
     */
    @Transactional
    public void notifyUpcomingScreenings() {

        List<User> users = userRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {

            List<Order> orders = user.getOrdersList();

            for (Order order : orders) {
                // 주문 상태가 'PAID'인 경우만 처리 + 이미 알림 보냈는지 체크
                if (!"PAID".equalsIgnoreCase(order.getStatus())) {
                    System.out.println("PAID 상태가 아닙니다.");
                    continue;
                }

                if (order.isNotified()) {
                    System.out.println("이미 이메일이 발송되었습니다.");
                    continue;
                }

                Screening screening = order.getScreening();

                LocalDateTime screeningDateTime = LocalDateTime.of(screening.getDate(), screening.getStart());

//                System.out.println("screening date : " + screeningDateTime);
//                System.out.println("screening title : " + screening.getMovie().getTitle());
//                System.out.println("screening price : " + screening.getPrice());
//                System.out.println("===============================================");
//
//                System.out.println("======================");
//                System.out.println(screeningDateTime);
//                System.out.println(screeningDateTime.isAfter(now));
//                System.out.println(screeningDateTime.isBefore(now.plusMinutes(30)));
//                System.out.println("=======================");

                // 현재 시간 기준으로 상영 시작 30분 전이면 알림
                if (screeningDateTime.isAfter(now) &&
                        screeningDateTime.isBefore(now.plusMinutes(30))) {

                    System.out.println("이메일 발송됨");

                    sendEmail(user.getEmail(), screening);

                    // ✅ 발송 완료 처리
                    order.setNotified(true);  // 엔티티 수정
                    orderRepository.save(order);  // 명시적 저장 (선택 사항)


                    break; // 한 번만 보내고 종료
                }
            }
        }
    }


    @Transactional
    public void notifyUpcomingScreeningsV2() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusMinutes(30);

        // 조건: 결제 완료(PAID) && 알림 미발송 && 30분 내 상영 시작
        List<Order> orders = orderRepository.findPaidOrdersToNotify(now, cutoff);

        for (Order order : orders) {

            Screening screening = order.getScreening();
            User user = order.getUser();

            LocalDateTime screeningDateTime = LocalDateTime.of(screening.getDate(), screening.getStart());

            if (screeningDateTime.isAfter(now) && screeningDateTime.isBefore(cutoff)) {
                System.out.println("이메일 발송됨");

                sendEmail(user.getEmail(), screening);

                // 알림 여부 true로 변경
                order.setNotified(true);
                orderRepository.save(order);
            }
        }
    }


    private void sendEmail(String email, Screening screening) {
        String subject = "[영화 예매 알림] 곧 상영이 시작됩니다!";
        String body = String.format(
                "안녕하세요!\n\n예매하신 영화 '%s'가 %s %s에 시작됩니다.\n" +
                        "상영 시작 30분 전이므로, 미리 도착해 주세요!\n\n감사합니다.",
                screening.getMovie().getTitle(),
                screening.getDate(),
                screening.getStart()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}