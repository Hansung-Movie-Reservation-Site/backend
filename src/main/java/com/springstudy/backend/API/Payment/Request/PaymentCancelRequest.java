package com.springstudy.backend.API.Payment.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequest {
    private Long orderId;
}