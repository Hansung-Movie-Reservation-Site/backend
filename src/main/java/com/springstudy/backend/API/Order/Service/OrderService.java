package com.springstudy.backend.API.Order.Service;

import com.springstudy.backend.API.Order.Model.SeatStatusMessage;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.*;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    private final RestTemplate restTemplate;

    private SimpMessagingTemplate messagingTemplate; // 메시지 전송용

    @Value("${api.RECOMMEND_API_KEY}")
    String RECOMMEND_API_KEY;

    public OrderService(OrderRepository orderRepository, ScreeningRepository screeningRepository,
                        SeatRepository seatRepository, UserRepository userRepository, TicketRepository ticketRepository,
                        RestTemplate restTemplate, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 여기서 웹소켓으로 통신 구현 필요
     */
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

        /**
        // ✅ 이미 예약된 좌석 확인
        boolean seatsAlreadyOrdered = ticketRepository.existsBySeatIdsAndOrderStatuses(seatIds, List.of("PENDING", "PAID"));
        if (seatsAlreadyOrdered) {
            throw new IllegalStateException("❌ 선택한 좌석 중 이미 예약된 좌석이 있습니다.");
        }
        */

        // ✅ 이미 예약된 좌석 확인 (상영 ID + 좌석 ID + 상태)
        boolean seatsAlreadyOrdered = ticketRepository.existsBySeatIdsAndScreeningIdAndOrderStatuses(
                seatIds,
                screeningId,
                List.of("PENDING", "PAID")
        );
        if (seatsAlreadyOrdered) {
            throw new IllegalStateException("❌ 선택한 좌석 중 이미 예약된 좌석이 있습니다.");
        }

        // ✅ 주문(Order) 생성
        Order order = Order.builder()
                .uuid(UUID.randomUUID().toString())  // 고유 주문 번호 생성
                .user(user)
                .screening(screening)
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
                // existingTicket.setUser(user);
                List<Recommand> recommandList = recommandMovie(100);
//                existingTicket.changeRecommandMovie(recommandList);
                tickets.add(existingTicket);
            } else {
                // ✅ 기존 티켓이 없으면 새롭게 생성
                Ticket newTicket = Ticket.builder()
                        .order(order)  // ✅ 주문과 연결
                        // .user(user)
                        .screening(screening)
                        .seat(seat)
                        .price(screening.getPrice())
                        .horizontal(seat.getHorizontal())
                        .vertical(seat.getVertical())
                        .build();
                List<Recommand> recommandList = recommandMovie(100);
                List<Recommand> nRecommandList = new ArrayList<>();
                for(int i=0; i<4; i++) {
                    nRecommandList.add(recommandList.get(i));
                }
//                newTicket.changeRecommandMovie(nRecommandList);
                tickets.add(newTicket);
            }
        }

        ticketRepository.saveAll(tickets); // ✅ 티켓 저장
        // user.setTicketList(tickets);

        // ✅ `Order`에 티켓 추가 후 다시 저장 (연관관계 설정)
        order.setTickets(tickets);
        orderRepository.save(order);

        // ✅ 웹소켓을 통해 좌석 상태(PENDING) broadcast
        SeatStatusMessage seatStatusMessage = new SeatStatusMessage(screeningId, seatIds, "PENDING");
        messagingTemplate.convertAndSend("/topic/seats", seatStatusMessage);

        return order;
    }

    public List<Recommand> recommandMovie(int movieId) {
        // 영화 추천 기능
        // 1. 추천할 영화 id로 추천 영화를 검색함. sample = 20
        // 2. 5개의 영화를 추천 엔티티에 저장함.
        ResponseEntity<Map> response = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("accept", "application/json");
        httpHeaders.set("Authorization", "Bearer "+RECOMMEND_API_KEY);
        HttpEntity<Map> httpEntity = new HttpEntity(httpHeaders);

        try{
            String url = "https://api.themoviedb.org/3/movie/"+movieId+"/similar?language=en-US&page=1";
            response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
        }
        catch(RestClientException e){
            log.error(e.getMessage());
            throw new CustomException("서버 응답 오류", ErrorCode.RESTAPI_ERROR);
        }

        if (response == null || response.getBody() == null) {
            throw new CustomException("영화 추천 API 응답이 비어 있습니다.", ErrorCode.API_RESPONSE_NULL);
        }
        List<Map<String, Object>> recommandResponse = (List<Map<String, Object>>) response.getBody().get("results");
        List<Recommand> recommandList = extractId(recommandResponse);

        //todo
        //영화 제목으로 영화 api 요청해서 검색.
        //검색된 영화를 recommand Movie와 Movie에 저장함.

        System.out.println(recommandList);

        return recommandList;
    }
    private List<Recommand> extractId(List<Map<String,Object>> recommandResponse){
        List<Recommand> recommandList = new ArrayList<>();
        if(recommandResponse == null){
            throw new CustomException("영화 추천 API 응답 형식이 맞지 않습니다.",ErrorCode.API_RESPONSE_MISMATCH);
        }
        for (Map<String, Object> r : recommandResponse) {
            if (r.containsKey("title")) {
                Recommand recommand = Recommand.builder()
                        .recommand_movie_id((Integer) r.get("id"))
                        .build();
                recommandList.add(recommand);
            }
        }
        return recommandList;
    }

    /**
     * ✅ 사용자 ID를 기반으로 주문 목록 조회 (단, CANCELED 상태 주문 제외)
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return orderRepository.findByUserAndStatusNot(user, "CANCELED");
    }

    /**
     * ✅ 주문 취소 (돈 지불하지 않고 주문 취소)
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + orderId));

        // ✅ 이미 취소된 주문인지 확인
        if ("CANCELED".equals(order.getStatus())) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        /**
         * ✅ 주문과 연결된 티켓 정보 삭제 처리
         */
        List<Ticket> tickets = order.getTickets();
        for (Ticket ticket : tickets) {
            ticket.setOrder(null);  // ✅ 주문 정보 제거
            // ticket.setUser(null);   // ✅ 사용자 정보 제거
        }

        order.updateStatus("CANCELED");
        order.setTid(null);
        orderRepository.save(order);
    }
}
