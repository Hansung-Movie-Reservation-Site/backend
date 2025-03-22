package com.springstudy.backend.API.Order.Controller;


import com.springstudy.backend.API.Order.Request.OrderRequest;
import com.springstudy.backend.API.Order.Service.OrderService;
import com.springstudy.backend.API.Repository.Entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderControllerV1 {

    private OrderService orderService;

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

    /**
     * ✅ 특정 사용자의 주문 목록 조회 API (CANCELED 상태 주문 제외)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * ✅ 주문 취소 API (ID 기반)
     */
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok("주문이 성공적으로 취소되었습니다. : " + orderId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
