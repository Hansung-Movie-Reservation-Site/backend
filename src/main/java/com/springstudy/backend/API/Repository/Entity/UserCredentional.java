package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "userCredentional")
public class UserCredentional {

    @Id
    @OneToOne
    @JoinColumn(name = "userid")
    private User user;

    @Column(nullable = false)
    private String password;
}
