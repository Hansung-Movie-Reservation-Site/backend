package com.springstudy.backend.API.Payment.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Order.Model.SeatStatusMessage;
import com.springstudy.backend.API.Payment.Response.KakaoReadyResponse;
import com.springstudy.backend.API.Repository.Entity.Order;
import com.springstudy.backend.API.Repository.Entity.Ticket;
import com.springstudy.backend.API.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KakaoPayService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

//    private final SimpMessagingTemplate messagingTemplate;

    private static KakaoReadyResponse kakaoReadyResponse;

    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String KAKAO_PAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String KAKAO_PAY_CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    // https://hs-cinemagix.duckdns.org
    // private static final String DOMAIN_URL = "http://localhost:8080/api/v1/payment/";
    private static final String DOMAIN_URL = "https://hs-cinemagix.duckdns.org/api/v1/payment/";


    @Value("${api.KAKAO_API_KEY}")
    String ADMIN_KEY;

    /*
    public KakaoPayService(RestTemplate restTemplate, ObjectMapper objectMapper, OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

     */

    public KakaoPayService(RestTemplate restTemplate, ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    /**
     * ✅ 결제 요청 (카카오페이 결제 PC 환경 URL 반환)
     */
    public String requestPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + orderId));

        // ✅ 이미 결제 완료 또는 취소된 주문이면 요청 거부
        String status = order.getStatus();
        if ("PAID".equalsIgnoreCase(status) || "CANCELED".equalsIgnoreCase(status)) {
            throw new IllegalStateException("❌ 이미 결제 완료되었거나 취소된 주문입니다. 결제 요청을 진행할 수 없습니다.");
        }

        if (ADMIN_KEY == null || ADMIN_KEY.isEmpty()) {
            throw new RuntimeException("❌ ADMIN_KEY가 설정되지 않았습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + ADMIN_KEY.trim());  // ✅ 변경된 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME");
        requestBody.put("partner_order_id", order.getUuid());
        requestBody.put("partner_user_id", order.getUser().getId());
        requestBody.put("item_name", "영화 티켓");
        requestBody.put("quantity", order.getTickets().size());
        requestBody.put("total_amount", order.getTotalAmount());
        requestBody.put("tax_free_amount", 0);

        // api 주소랑 동일하게 설정
        requestBody.put("approval_url", DOMAIN_URL + "success");
        requestBody.put("cancel_url", DOMAIN_URL + "cancel");
        requestBody.put("fail_url", DOMAIN_URL + "fail");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            // response = restTemplate.exchange(KAKAO_PAY_READY_URL, HttpMethod.POST, entity, String.class);

            kakaoReadyResponse = restTemplate.postForObject(KAKAO_PAY_READY_URL, entity, KakaoReadyResponse.class);
            kakaoReadyResponse.setUuid(order.getUuid());

        } catch (HttpClientErrorException e) {
            System.err.println("❌ 카카오페이 API 요청 오류: " + e.getMessage());
            throw new RuntimeException("❌ 카카오페이 API 요청 실패: " + e.getResponseBodyAsString());
        }

        try {
            // JsonNode jsonNode = objectMapper.readTree(response.getBody());
            // return jsonNode.get("next_redirect_pc_url").asText();

            return kakaoReadyResponse.getNext_redirect_pc_url();
        } catch (Exception e) {
            throw new RuntimeException("❌ 카카오페이 결제 요청 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 결제 요청 (카카오페이 결제 모바일 웹 환경 URL 반환)
     */
    public String requestPaymentMobileWeb(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + orderId));

        // ✅ 이미 결제 완료 또는 취소된 주문이면 요청 거부
        String status = order.getStatus();
        if ("PAID".equalsIgnoreCase(status) || "CANCELED".equalsIgnoreCase(status)) {
            throw new IllegalStateException("❌ 이미 결제 완료되었거나 취소된 주문입니다. 결제 요청을 진행할 수 없습니다.");
        }


        if (ADMIN_KEY == null || ADMIN_KEY.isEmpty()) {
            throw new RuntimeException("❌ ADMIN_KEY가 설정되지 않았습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + ADMIN_KEY.trim());  // ✅ 변경된 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME");
        requestBody.put("partner_order_id", order.getUuid());
        requestBody.put("partner_user_id", order.getUser().getId());
        requestBody.put("item_name", "영화 티켓");
        requestBody.put("quantity", order.getTickets().size());
        requestBody.put("total_amount", order.getTotalAmount());
        requestBody.put("tax_free_amount", 0);

        // api 주소랑 동일하게 설정
        requestBody.put("approval_url", DOMAIN_URL + "success");
        requestBody.put("cancel_url", DOMAIN_URL + "cancel");
        requestBody.put("fail_url", DOMAIN_URL + "fail");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            // response = restTemplate.exchange(KAKAO_PAY_READY_URL, HttpMethod.POST, entity, String.class);

            kakaoReadyResponse = restTemplate.postForObject(KAKAO_PAY_READY_URL, entity, KakaoReadyResponse.class);
            kakaoReadyResponse.setUuid(order.getUuid());

        } catch (HttpClientErrorException e) {
            System.err.println("❌ 카카오페이 API 요청 오류: " + e.getMessage());
            throw new RuntimeException("❌ 카카오페이 API 요청 실패: " + e.getResponseBodyAsString());
        }

        try {
            return kakaoReadyResponse.getNext_redirect_mobile_url();
        } catch (Exception e) {
            throw new RuntimeException("❌ 카카오페이 결제 요청 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 결제 요청 (카카오페이 결제 모바일 app 환경 URL 반환)
     */
    public String requestPaymentMobileApp(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + orderId));


        // ✅ 이미 결제 완료 또는 취소된 주문이면 요청 거부
        String status = order.getStatus();
        if ("PAID".equalsIgnoreCase(status) || "CANCELED".equalsIgnoreCase(status)) {
            throw new IllegalStateException("❌ 이미 결제 완료되었거나 취소된 주문입니다. 결제 요청을 진행할 수 없습니다.");
        }

        if (ADMIN_KEY == null || ADMIN_KEY.isEmpty()) {
            throw new RuntimeException("❌ ADMIN_KEY가 설정되지 않았습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + ADMIN_KEY.trim());  // ✅ 변경된 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME");
        requestBody.put("partner_order_id", order.getUuid());
        requestBody.put("partner_user_id", order.getUser().getId());
        requestBody.put("item_name", "영화 티켓");
        requestBody.put("quantity", order.getTickets().size());
        requestBody.put("total_amount", order.getTotalAmount());
        requestBody.put("tax_free_amount", 0);

        // api 주소랑 동일하게 설정
        requestBody.put("approval_url", DOMAIN_URL + "success");
        requestBody.put("cancel_url", DOMAIN_URL + "cancel");
        requestBody.put("fail_url", DOMAIN_URL + "fail");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            // response = restTemplate.exchange(KAKAO_PAY_READY_URL, HttpMethod.POST, entity, String.class);

            kakaoReadyResponse = restTemplate.postForObject(KAKAO_PAY_READY_URL, entity, KakaoReadyResponse.class);
            kakaoReadyResponse.setUuid(order.getUuid());

        } catch (HttpClientErrorException e) {
            System.err.println("❌ 카카오페이 API 요청 오류: " + e.getMessage());
            throw new RuntimeException("❌ 카카오페이 API 요청 실패: " + e.getResponseBodyAsString());
        }

        try {
            return kakaoReadyResponse.getNext_redirect_app_url();
        } catch (Exception e) {
            throw new RuntimeException("❌ 카카오페이 결제 요청 실패: " + e.getMessage());
        }
    }

    @Transactional
    public String approvePayment(String pgToken) {

        Order order = orderRepository.findByUuid(kakaoReadyResponse.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + kakaoReadyResponse.getUuid()));

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("❌ 이미 결제된 주문이거나 취소되거나 유효하지 않은 주문입니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + ADMIN_KEY.trim());  // ✅ 변경된 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME");
        requestBody.put("tid", kakaoReadyResponse.getTid());
        requestBody.put("partner_order_id", order.getUuid());
        requestBody.put("partner_user_id", order.getUser().getId().toString());
        requestBody.put("pg_token", pgToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_PAY_APPROVE_URL, HttpMethod.POST, entity, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // ✅ 결제 승인 후 새로운 TID 업데이트
            String newTid = jsonNode.get("tid").asText();
            order.updateTid(newTid);
            order.updateStatus("PAID");

            orderRepository.save(order);

            /*
            // ✅ 웹소켓을 통해 좌석 상태(PAID) broadcast
            SeatStatusMessage seatStatusMessage = new SeatStatusMessage(
                    order.getScreening().getId(),
                    order.getTickets().stream().map(ticket -> ticket.getSeat().getId()).toList(),
                    "PAID");
            messagingTemplate.convertAndSend("/topic/seats", seatStatusMessage);

             */

            return "✅ 결제 성공! TID: " + newTid;
        } catch (Exception e) {
            throw new RuntimeException("❌ 카카오페이 결제 승인 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 결제 취소 (환불 처리)
     */
    @Transactional
    public void cancelPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주문 ID가 존재하지 않습니다: " + orderId));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + ADMIN_KEY.trim());  // ✅ 변경된 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME");
        requestBody.put("tid", order.getTid());
        requestBody.put("cancel_amount", order.getTotalAmount());
        requestBody.put("cancel_tax_free_amount", 0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        restTemplate.exchange(KAKAO_PAY_CANCEL_URL, HttpMethod.POST, entity, String.class);

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

        /*
        // ✅ 웹소켓을 통해 좌석 상태(CANCELED) broadcast
        SeatStatusMessage seatStatusMessage = new SeatStatusMessage(
                order.getScreening().getId(),
                order.getTickets().stream().map(ticket -> ticket.getSeat().getId()).toList(),
                "CANCELED");
        messagingTemplate.convertAndSend("/topic/seats", seatStatusMessage);

         */
    }

}
