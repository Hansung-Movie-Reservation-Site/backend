package com.springstudy.backend.API.Repository.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity

@Table(name = "review_like")
@IdClass(ReviewLikeId.class)

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReviewLike {
    @Id
    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "reviewid")
    private Review review;

}
