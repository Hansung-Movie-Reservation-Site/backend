package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.MyTheather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyTheatherRepository extends JpaRepository<MyTheather, Integer> {
    List<MyTheather> findByUserId(Long user_id);
}
