package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
        //(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "user")
@JsonIgnoreProperties({"ordersList", "userTickets", "aiList"}) // 🚨 추가: Order 리스트 무시
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

    // ✅ Order와 1:N 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("user") // 🚨 추가: Order에서 User 직렬화 무시
    private List<Order> ordersList = new ArrayList<>(); // ✅ 초기값 설정

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

    /**
     * 추가
     * ✅ 사용자의 모든 주문에서 티켓 리스트 반환
     */
    public List<Ticket> getUserTickets() {
        List<Ticket> ticketList = new ArrayList<>();

        if (ordersList != null) {
            for (Order order : ordersList) {
                if (order.getTickets() != null) {
                    ticketList.addAll(order.getTickets());
                }
            }
        }
        return ticketList;
    }

    /**
     * 주석 처리
     */
//    @Column
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Ticket> ticketList = null;

//    public void setTicketList(List<Ticket> ticketList) {
//        this.ticketList = ticketList;
//    }

    @Column
    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<AI> aiList = new ArrayList<>();

    @Column(nullable = true)
    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy= "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<MyTheater> myTheaterList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "review_like",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "reviewid")
    )
    private Set<Review> likedReviews;

    @Transactional
    public List<MyTheater> getMyTheaterList() {
        myTheaterList.size();
        for(int i = 0; i< myTheaterList.size(); i++){
            System.out.println("getSpot_id: "+ myTheaterList.get(i).getSpot_id());
        }
        return myTheaterList;
    }

    public void setMyTheaterList(List<MyTheater> myTheaterList) {
        this.myTheaterList = myTheaterList;
    }
}
