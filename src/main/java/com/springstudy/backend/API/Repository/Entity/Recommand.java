package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Recommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer recommand_movie_id;

    @ManyToOne
    @JoinColumn(nullable = true, name = "user_id", foreignKey = @ForeignKey(name = "FK_recommand_TO_user"))
    private User user;

//    @ManyToOne
//    @JoinColumn(nullable = true, name = "ticket_id", foreignKey = @ForeignKey(name = "FK_ticket_TO_recommand"))
//    private Ticket ticket;
    // movie와 OneToOne 할까 생각했으나
    // 복잡해지고 성능 생각해서 id만 저장하고 필요할 때 검색될 때 id로 그때그때 조회하는 걸로.

    //todo
    //예매내역 엔티티와 연결할 것.
}
