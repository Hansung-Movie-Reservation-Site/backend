package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_seat_position",
                columnNames = {
                        "roomid",
                        "horizontal",
                        "vertical"}
                )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roomid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_room_TO_seat_1"))
    private Room room;

    @Column(nullable = false, length = 1)
    private String horizontal;

    @Column(nullable = false)
    private int vertical;
}