package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyTheather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long spot_id;

    @JoinColumn(name = "userid", nullable = false,
            foreignKey = @ForeignKey(name = "FK_mytheather_TO_user_1"))
    @JsonIgnore
    @ManyToOne(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;
}
