package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.MyTheater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyTheaterRepository extends JpaRepository<MyTheater, Integer> {
    List<MyTheater> findByUserId(Long user_id);
    void deleteMyTheaterByUser_Id(Long user_id);
}
