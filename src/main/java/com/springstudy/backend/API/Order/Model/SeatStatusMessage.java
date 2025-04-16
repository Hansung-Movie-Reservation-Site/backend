package com.springstudy.backend.API.Order.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class SeatStatusMessage {
    private Long screeningId;
    private List<Long> seatIds;
    private String status; // "PENDING", "PAID", "CANCELLED", etc.

    public SeatStatusMessage() {}

    public SeatStatusMessage(Long screeningId, List<Long> seatIds, String status) {
        this.screeningId = screeningId;
        this.seatIds = seatIds;
        this.status = status;
    }
}
