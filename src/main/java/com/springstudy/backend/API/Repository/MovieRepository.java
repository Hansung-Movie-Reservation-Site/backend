package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByMovieId(Integer movieId);
    // boolean existsByMovieId(Integer movieId);

    // boolean existsByMovieCode(String movieCode);
}