package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spot",
        uniqueConstraints = { @UniqueConstraint(name = "unique_region_name", columnNames = { "regionid", "name" }) })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "regionid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "spot_ibfk_1"))
    private Region region;
}
