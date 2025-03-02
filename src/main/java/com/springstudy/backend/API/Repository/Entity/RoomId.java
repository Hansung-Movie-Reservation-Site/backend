package com.springstudy.backend.API.Repository.Entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class RoomId implements Serializable {
    private Long spotId;
    private Long id;
}