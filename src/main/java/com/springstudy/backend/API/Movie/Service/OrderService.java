package com.springstudy.backend.API.Movie.Service;

import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public OrderService(OrderRepository orderRepository, ScreeningRepository screeningRepository,
                        SeatRepository seatRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * ✅ Screening ID, Seat ID 리스트, User ID로 주문 생성
     */
    @Transactional
    public Order createOrder(Long userId, List<Long> seatIds, Long screeningId) {
        // ✅ 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 유효하지 않은 사용자 ID"));

        // ✅ 상영 정보 조회
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 유효하지 않은 상영 ID"));

        // ✅ 선택한 좌석 조회
        List<Seat> seats = seatRepository.findByIdIn(seatIds);
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("❌ 유효한 좌석 ID가 없습니다.");
        }

        // ✅ 이미 예약된 좌석 확인
        boolean seatsAlreadyOrdered = ticketRepository.existsBySeatIdsAndOrderStatuses(seatIds, List.of("PENDING", "PAID"));
        if (seatsAlreadyOrdered) {
            throw new IllegalStateException("❌ 선택한 좌석 중 이미 예약된 좌석이 있습니다.");
        }

        // ✅ 주문(Order) 생성
        Order order = Order.builder()
                .uuid(UUID.randomUUID().toString())  // 고유 주문 번호 생성
                .user(user)
                .status("PENDING")  // ✅ 주문 상태 초기화 (결제 대기)
                .totalAmount(screening.getPrice() * seats.size()) // 총 가격 계산
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);  // ✅ Order 저장

        // ✅ Ticket 데이터 생성
        List<Ticket> tickets = new ArrayList<>();  // ✅ `ArrayList` 사용하여 수정 가능 리스트 생성
        for (Seat seat : seats) {
            // ✅ 기존 티켓 조회
            Ticket existingTicket = ticketRepository.findByScreeningAndSeat(screening, seat);

            if (existingTicket != null) {
                // ✅ 기존 티켓이 존재하면 Order ID와 User ID 업데이트
                existingTicket.setOrder(order);
                existingTicket.setUser(user);
                tickets.add(existingTicket);
            } else {
                // ✅ 기존 티켓이 없으면 새롭게 생성
                Ticket newTicket = Ticket.builder()
                        .order(order)  // ✅ 주문과 연결
                        .user(user)
                        .screening(screening)
                        .seat(seat)
                        .price(screening.getPrice())
                        .horizontal(seat.getHorizontal())
                        .vertical(seat.getVertical())
                        .build();
                tickets.add(newTicket);
            }
        }

        /**
         *
         */
        ticketRepository.saveAll(tickets); // ✅ 티켓 저장

        // ✅ `Order`에 티켓 추가 후 다시 저장 (연관관계 설정)
        order.setTickets(tickets);
        orderRepository.save(order);

        return order;
    }
}
