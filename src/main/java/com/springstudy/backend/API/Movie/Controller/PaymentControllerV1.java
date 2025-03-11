package com.springstudy.backend.API.Movie.Controller;

import com.springstudy.backend.API.Movie.Model.Request.PaymentCancelRequest;
import com.springstudy.backend.API.Movie.Model.Request.PaymentRequest;
import com.springstudy.backend.API.Movie.Service.KakaoPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentControllerV1 {

    private final KakaoPayService kakaoPayService;

    public PaymentControllerV1(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

    /**
     * ✅ 1️⃣ 결제 요청 API (카카오페이 결제 URL 반환)
     * 요청 예시:
     * ```json
     * {
     *   "orderId": 1
     * }
     * ```
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestPayment(@RequestBody PaymentRequest request) {
        String paymentUrl = kakaoPayService.requestPayment(request.getOrderId());
        return ResponseEntity.ok(paymentUrl); // ✅ 클라이언트가 카카오페이 페이지로 리디렉션해야 함
    }

    /**
     * ✅ 2️⃣ 결제 승인 API (카카오페이 결제 URL 반환되면서 자동으로 진행)
     * 예시: http://localhost:8080/api/v1/payment/success?pg_token=0b61d76630ecff3bdd6a
     */
    @GetMapping("/success")
    public ResponseEntity<String> approvePayment(@RequestParam("pg_token")
                                                     String pg_token) {
        String result = kakaoPayService.approvePayment(pg_token);
        return ResponseEntity.ok(result);
    }

    /**
     * ✅ 3️⃣ 결제 취소 API (카카오페이 결제 취소)
     * 요청 예시:
     * ```json
     * {
     *   "orderId": 1
     * }
     * ```
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelRequest request) {
        kakaoPayService.cancelPayment(request.getOrderId());
        return ResponseEntity.ok("✅ 결제가 취소되었습니다: " + request.getOrderId());
    }
}