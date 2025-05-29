package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.MyTheater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MyTheaterRepository extends JpaRepository<MyTheater, Integer> {
    List<MyTheater> findByUserId(Long user_id);
    void deleteMyTheaterByUser_Id(Long user_id);

    @Transactional
    @Modifying
    @Query("DELETE FROM MyTheater mt WHERE mt.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);
}
