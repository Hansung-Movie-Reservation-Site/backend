package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "AI")
public class AI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movieId;

    @Column(columnDefinition = "text", nullable = false)
    private String reason;

    @JoinColumn(nullable = false,name = "userid")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    User user;

}
