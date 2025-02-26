package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByRegionName(String regionName);
}