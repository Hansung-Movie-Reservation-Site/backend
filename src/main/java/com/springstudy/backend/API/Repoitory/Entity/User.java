package com.springstudy.backend.API.Repoitory.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserCredentional user_credentional;

    public void changeEmail(String email) {
        this.email = email;
    }
    public void changeUsername(String username) {
        this.username = username;
    }
    public void setUserCredentional(UserCredentional user_credentional) {
        this.user_credentional = user_credentional;
    }

    public void changePassword(String password) {
        getUser_credentional().changePassword(password);
    }
}
