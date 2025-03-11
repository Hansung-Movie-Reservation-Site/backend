package com.springstudy.backend.API.Movie.Model.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequest {
    private Long orderId;
}