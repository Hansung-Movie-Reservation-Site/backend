package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnore  // ✅ 순환 참조 방지
    private UserCredentional user_credentional;

    @Column
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> ticketList = null;

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
    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
