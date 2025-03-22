package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_room_position",
                columnNames = {
                        "spotid",
                        "roomnumber"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    // @MapsId("spotId")
    @JoinColumn(name = "spotid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_room_TO_spot_1"))
    private Spot spot;

    @Column(nullable = false)
    private Long roomnumber;
}