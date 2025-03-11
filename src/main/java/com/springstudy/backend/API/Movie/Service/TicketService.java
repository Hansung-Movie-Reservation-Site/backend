//package com.springstudy.backend.API.Movie.Service;
//
//import com.springstudy.backend.API.Repository.Entity.Screening;
//import com.springstudy.backend.API.Repository.Entity.Seat;
//import com.springstudy.backend.API.Repository.Entity.Ticket;
//import com.springstudy.backend.API.Repository.Entity.User;
//import com.springstudy.backend.API.Repository.ScreeningRepository;
//import com.springstudy.backend.API.Repository.SeatRepository;
//import com.springstudy.backend.API.Repository.TicketRepository;
//import com.springstudy.backend.API.Repository.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class TicketService {
//
//    private final TicketRepository ticketRepository;
//    private final ScreeningRepository screeningRepository;
//    private final SeatRepository seatRepository;
//    private final UserRepository userRepository;
//
//    public TicketService(TicketRepository ticketRepository, ScreeningRepository screeningRepository,
//                         SeatRepository seatRepository, UserRepository userRepository) {
//        this.ticketRepository = ticketRepository;
//        this.screeningRepository = screeningRepository;
//        this.seatRepository = seatRepository;
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * ✅ Screening ID, Seat ID 리스트, User ID를 이용하여 Ticket 생성 및 저장
//     */
//    @Transactional
//    public List<Ticket> createTickets(Long screeningId, List<Long> seatIds, Long userId) {
//        // 1️⃣ Screening ID로 Screening 데이터 조회
//        Screening screening = screeningRepository.findById(screeningId)
//                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 Screening ID가 존재하지 않습니다: " + screeningId));
//
//        // 2️⃣ Seat ID 리스트로 Seat 데이터 조회
//        List<Seat> seats = seatRepository.findByIdIn(seatIds);
//        if (seats.isEmpty()) {
//            throw new IllegalArgumentException("❌ 유효한 Seat ID가 없습니다.");
//        }
//
//        // 3️⃣ User ID로 User 데이터 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 User ID가 존재하지 않습니다: " + userId));
//
//        // 4️⃣ 중복된 Ticket이 있는지 확인
//        for (Seat seat : seats) {
//            boolean exists = ticketRepository.existsByScreeningAndSeat(screening, seat);
//            if (exists) {
//                System.err.println("❌ 이미 예약된 좌석입니다: " + seat.getHorizontal() + seat.getVertical()); // ✅ 로그 출력 추가
//                throw new IllegalStateException("❌ 이미 예약된 좌석입니다: " + seat.getHorizontal() + seat.getVertical());
//            }
//        }
//
//        // 5️⃣ Ticket 엔티티 생성 및 저장
//        List<Ticket> tickets = seats.stream()
//                .map(seat -> Ticket.builder()
//                        .screening(screening)
//                        .seat(seat)
//                        .user(user)
//                        .price(screening.getPrice()) // ✅ Screening 가격 적용
//                        .horizontal(seat.getHorizontal())
//                        .vertical(seat.getVertical())
//                        .build())
//                .collect(Collectors.toList());
//
//        return ticketRepository.saveAll(tickets);
//    }
//}