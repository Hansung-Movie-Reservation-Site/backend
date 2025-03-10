package com.springstudy.backend.API.Movie.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponseDTO {
    private Long seatId;
    private String horizontal;
    private int vertical;
    private boolean reserved; // ✅ 예약 여부 추가
}
