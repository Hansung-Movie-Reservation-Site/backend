package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Broadcast;
import com.springstudy.backend.API.Repository.Entity.Region;
import com.springstudy.backend.API.Repository.Entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {

}