package com.springstudy.backend.API.Repository.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_movie_review",
                columnNames = {"movieid", "userid"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float rating;

    @Column(nullable = false, length = 500)
    private String review;

    @Column(nullable = false)
    private boolean spoiler;

    @Column(nullable = false)
    private LocalDate reviewDate = LocalDate.now();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_review_TO_user_1"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "movieid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_review_TO_movie_1"))
    private Movie movie;

    @JsonIgnore
    @ManyToMany(mappedBy = "likedReviews")
    private Set<User> likedByUsers;

    public void setRating(Float rating) {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("별점은 0.0 ~ 5.0 사이여야 합니다.");
        }
        this.rating = rating;
    }

    public void setReview(String review) {
        if (review == null || review.trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용을 입력해 주세요.");
        }
        if (review.length() < 10) { // 최소 10자 이상 입력
            throw new IllegalArgumentException("리뷰는 최소 10자 이상 입력해야 합니다.");
        }
        this.review = review;
    }

    public Boolean getSpoiler() { // @Getter있는데 r.getSpoiler()에서 자꾸 에러나서 추가함;;
        return spoiler;
    }

    public void setSpoiler(Boolean spoiler) {
        this.spoiler = spoiler;
    }
}