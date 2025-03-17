package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "ticket",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_ticket_constraint",
                columnNames = {
                        "screeningid",
                        "seatid",
                        "horizontal",
                        "vertical",
                        "price",
                        "ordersid",
                        "recommand_movie"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"order", "user"}) // 🚨 추가: Order, User 직렬화 방지
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "screeningid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ticket_TO_screening_1"))
    private Screening screening;

    @ManyToOne
    @JoinColumn(name = "seatid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ticket_TO_seat_1"))
    private Seat seat;

    @Column(nullable = false, length = 1)
    private String horizontal;

    @Column(nullable = false)
    private int vertical;

    @Column(nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "ordersid", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_ticket_TO_orders"))
    @JsonIgnoreProperties("tickets") // 🚨 추가: Order에서 티켓 직렬화 방지
    private Order order;

//    @ManyToOne
//    @JoinColumn(name = "userid", referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "FK_ticket_TO_user"))
//    @JsonIgnoreProperties("userTickets") // 🚨 추가: User에서 티켓 직렬화 방지
//    private User user; // ✅ 구매한 사용자 ID를 외래키로 참조

//    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Recommand> recommand_movie;

//    public void changeRecommandMovie(List<Recommand> recommand_movie){
//        this.recommand_movie = recommand_movie;
//    }

}
