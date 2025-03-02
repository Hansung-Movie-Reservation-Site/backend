package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @EmbeddedId
    private RoomId id;

    @Column(nullable = false)
    private int horizontal;

    @Column(nullable = false, length = 1)
    private String vertical;

    @ManyToOne
    @MapsId("spotId")
    @JoinColumn(name = "spotid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_spot_TO_room_1"))
    private Spot spot;
}