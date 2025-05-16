package com.springstudy.backend.API.Repository.Entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor

public class ReviewLikeId implements Serializable {

    private Long user;
    private Long review;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewLikeId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(review, that.review);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, review);
    }
}
