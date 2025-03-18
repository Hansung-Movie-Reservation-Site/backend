package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "screening",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_screening_constraint",
                columnNames = {
                        "movieid",
                        "roomid",
                        "date",
                        "start",
                        "finish",
                        "price"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movieid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_screening_TO_movie_1"))
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "roomid", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_screening_TO_room_1"))
    private Room room;

    @Column(nullable = false)
    private LocalDate date;  // ✅ 상영 날짜

    @Column(nullable = false)
    private LocalTime start;  // ✅ 상영 시작 시간

    @Column(nullable = false)
    private LocalTime finish;  // ✅ 상영 종료 시간

    @Column(nullable = false)
    private int price;

    /**
     * ✅ 상영 날짜 검증: 개봉일 이전인지 확인
     */
    public void validateScreeningDate() {
        if (movie == null || movie.getReleaseDate() == null) {
            throw new IllegalArgumentException("영화 개봉일 정보가 없습니다.");
        }
        if (!date.isBefore(movie.getReleaseDate())) {
            throw new IllegalArgumentException("❌ 상영 날짜는 영화 개봉일 이전이어야 합니다.");
        }
    }

    /**
     * ✅ 상영 종료 시간 검증: 영화 상영 시간 반영하여 30분 단위로 설정
     */
    public void calculateFinishTime() {
        if (movie == null || movie.getRuntime() == null) {
            throw new IllegalArgumentException("영화 정보가 없거나 상영 시간이 설정되지 않았습니다.");
        }

        // 기본 종료 시간 계산 (start + runtime 분 단위 변환)
        LocalTime calculatedFinishTime = start.plusMinutes(movie.getRuntime());

        // 30분 단위로 조정
        int remainder = calculatedFinishTime.getMinute() % 30;
        if (remainder > 0) {
            calculatedFinishTime = calculatedFinishTime.plusMinutes(30 - remainder);
        }

        if (!calculatedFinishTime.isAfter(start)) {
            throw new IllegalArgumentException("❌ 상영 종료 시간은 시작 시간 이후여야 합니다.");
        }

        this.finish = calculatedFinishTime;
    }
}