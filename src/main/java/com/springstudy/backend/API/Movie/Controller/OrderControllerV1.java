package com.springstudy.backend.API.Movie.Controller;


import com.springstudy.backend.API.Movie.Model.Request.OrderRequest;
import com.springstudy.backend.API.Movie.Service.OrderService;
import com.springstudy.backend.API.Repository.Entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
public class OrderControllerV1 {

    private final OrderService orderService;

    public OrderControllerV1(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * ✅ 주문 생성 API (POST 요청)
     * 요청 예시: POST api/v1/orders
     * body에 json 작성
     * {
     *   "userId": 1,
     *   "screeningId": 10,
     *   "seatIds": [101, 102, 103]
     * }
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request.getUserId(), request.getSeatIds(), request.getScreeningId());
        return ResponseEntity.ok(order);
    }

    // 주문 조회 작성 필요
}
