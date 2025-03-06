package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "ticket",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_ticket_constraint",
                columnNames = { "screeningid", "userid", "horizontal", "vertical", "purchase_time", "price"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_ticket_TO_user"))
    private User user; // ✅ 구매한 사용자 ID를 외래키로 참조

    @ManyToOne
    @JoinColumn(name = "screeningid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ticket_TO_screening_1"))
    private Screening screening;

    @ManyToOne
    @JoinColumn(name = "seatid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ticket_TO_seat_1"))
    private Seat seat;

    @Column(nullable = false)
    private LocalDate purchase_time;  // ✅ 구매 시간

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 1)
    private String horizontal;

    @Column(nullable = false)
    private int vertical;

}
