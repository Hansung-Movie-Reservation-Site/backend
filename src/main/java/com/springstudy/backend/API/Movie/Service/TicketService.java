//package com.springstudy.backend.API.Movie.Service;
//
//import com.springstudy.backend.API.Repository.Entity.*;
//import com.springstudy.backend.API.Repository.ScreeningRepository;
//import com.springstudy.backend.API.Repository.SeatRepository;
//import com.springstudy.backend.API.Repository.TicketRepository;
//import com.springstudy.backend.API.Repository.UserRepository;
//import com.springstudy.backend.Common.ErrorCode.CustomException;
//import com.springstudy.backend.Common.ErrorCode.ErrorCode;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//public class TicketService {
//
//    private final TicketRepository ticketRepository;
//    private final ScreeningRepository screeningRepository;
//    private final SeatRepository seatRepository;
//    private final UserRepository userRepository;
//
//    private final RestTemplate restTemplate;
//
//    @Value("${api.RECOMMEND_API_KEY}")
//    String RECOMMEND_API_KEY;
//
//
//    public TicketService(TicketRepository ticketRepository, ScreeningRepository screeningRepository,
//                         SeatRepository seatRepository, UserRepository userRepository, RestTemplate restTemplate) {
//        this.ticketRepository = ticketRepository;
//        this.screeningRepository = screeningRepository;
//        this.seatRepository = seatRepository;
//        this.userRepository = userRepository;
//        this.restTemplate = restTemplate;
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
//        for(Ticket ticket : tickets) {
//            List<Recommand> recommandList = recommandMovie(ticket.getScreening().getMovie().getMovieId());
//            ticket.changeRecommandMovie(recommandList);
//        }
//
//        return ticketRepository.saveAll(tickets);
//    }
//
////    public List<Recommand> recommandMovie(int movieId) {
////        // 영화 추천 기능
////        // 1. 추천할 영화 id로 추천 영화를 검색함. sample = 20
////        // 2. 5개의 영화를 추천 엔티티에 저장함.
////        ResponseEntity<Map> response = null;
////        HttpHeaders httpHeaders = new HttpHeaders();
////        HttpEntity<Map> httpEntity = new HttpEntity(httpHeaders);
////        httpEntity.getHeaders().set("accept", "application/json");
////        httpEntity.getHeaders().set("Authorization", "Bearer "+RECOMMEND_API_KEY);
////
////        try{
////            String url = "https://api.themoviedb.org/3/movie/"+movieId+"/similar?language=en-US&page=1";
////            response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
////        }
////        catch(RestClientException e){
////            log.error(e.getMessage());
////            throw new CustomException("서버 응답 오류", ErrorCode.RESTAPI_ERROR);
////        }
////
////        if (response == null || response.getBody() == null) {
////            throw new CustomException("영화 추천 API 응답이 비어 있습니다.", ErrorCode.API_RESPONSE_NULL);
////        }
////        List<Map<String, Object>> recommandResponse = (List<Map<String, Object>>) response.getBody().get("results");
////        List<Recommand> recommandList = extractId(recommandResponse);
////
////        //todo
////        //영화 제목으로 영화 api 요청해서 검색.
////        //검색된 영화를 recommand Movie와 Movie에 저장함.
////
////        System.out.println(recommandList);
////
////        return recommandList;
////    }
////    private List<Recommand> extractId(List<Map<String,Object>> recommandResponse){
////        List<Recommand> recommandList = new ArrayList<>();
////        if(recommandResponse == null){
////            throw new CustomException("영화 추천 API 응답 형식이 맞지 않습니다.",ErrorCode.API_RESPONSE_MISMATCH);
////        }
////        for (Map<String, Object> r : recommandResponse) {
////            if (r.containsKey("title")) {
////                Recommand recommand = Recommand.builder()
////                        .recommand_movie_id((Integer) r.get("id"))
////                        .build();
////                recommandList.add(recommand);
////            }
////        }
////        return recommandList;
////    }
//}