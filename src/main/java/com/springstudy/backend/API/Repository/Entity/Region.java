package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "region")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 새로운 기본 키 추가 (AUTO_INCREMENT)

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;  // 유니크 제약조건 추가
}