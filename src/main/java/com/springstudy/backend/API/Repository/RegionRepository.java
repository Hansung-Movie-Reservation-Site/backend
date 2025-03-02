package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RegionRepository extends JpaRepository<Region, String> {
    Optional<Region> findByName(String name);
}