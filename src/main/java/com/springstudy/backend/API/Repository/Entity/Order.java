package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") // ✅ 예약어 충돌 방지
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;  // 고유 주문 번호 (UUID)

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false,
            foreignKey = @ForeignKey(name = "FK_order_user"))
    private User user;  // 주문한 사용자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")  // ✅ 직렬화 문제 방지 (무한 루프 방지)
    private List<Ticket> tickets;  // ✅ 한 개의 주문에 여러 개의 티켓이 포함됨

    @Column(nullable = false)
    private String status;  // 주문 상태 (PENDING, PAID, CANCELLED, TIME_OUT)

    @Column(nullable = false)
    private int totalAmount;  // 총 주문 금액

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 주문 생성 시간

    @Column()
    private String tid;  // 카카오페이 결제 고유 번호 (결제 승인 시 저장)

    /**
     * ✅ 주문 상태 변경
     */
    public void updateStatus(String status) {
        this.status = status;
    }

    /**
     * ✅ 카카오페이 결제 승인 후 tid 저장
     */
    public void updateTid(String tid) {
        this.tid = tid;
    }

    /**
     * ✅ `tid`가 `null`일 경우 기본값 반환
     */
    public String getTid() {
        return tid != null ? tid : "결제 대기 중";
    }

}
