package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Recommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommandRepository extends JpaRepository<Recommand, Long> {
}
