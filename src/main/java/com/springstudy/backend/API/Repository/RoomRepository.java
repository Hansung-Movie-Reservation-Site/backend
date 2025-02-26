package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Room;
import com.springstudy.backend.API.Repository.Entity.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomRepository extends JpaRepository<Room, RoomId> {

}