package com.springstudy.backend.API.Order.Request;

import lombok.Getter;
import java.util.List;

@Getter
public class OrderRequest {
    private Long userId;
    private Long screeningId;
    private List<Long> seatIds;
}
