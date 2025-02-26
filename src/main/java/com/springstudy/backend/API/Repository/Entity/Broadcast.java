package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "broadcast")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Broadcast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime start;

    @Column(nullable = false)
    private LocalTime finish;

    @Column(nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "movieid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "broadcast_ibfk_1"))
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "regionid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "broadcast_ibfk_2"))
    private Region region;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "spotid", referencedColumnName = "spotid", nullable = false),
            @JoinColumn(name = "roomid", referencedColumnName = "id", nullable = false)
    })
    private Room room;
}