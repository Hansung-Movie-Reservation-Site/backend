package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.User.UserResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);







    @Query("SELECT new com.springstudy.backend.API.User.UserResponseDTO(u.id, u.username, u.email) FROM User u")
    List<UserResponseDTO> findAllBasicUsers();
}