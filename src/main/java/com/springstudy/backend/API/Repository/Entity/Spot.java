package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "spot")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne
    @JoinColumn(name = "regionid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "spot_ibfk_1"))
    private Region region;
}
