package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "user_credentional")
public class UserCredentional {
//    @Id
//    private Long id;

    @Id
    @OneToOne
    @JoinColumn(name = "userid")
    private User user;

    @Column(nullable = false)
    private String password;

    public void changePassword(String password) {
        this.password = password;
    }
}
