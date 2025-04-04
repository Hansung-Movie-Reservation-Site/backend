package com.springstudy.backend.API.Screening.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ScreeningAddDTO {
    private LocalDate date;
    private Long roomNumber;
    private String movieTitle;
    private LocalTime start;
    private LocalTime finish;
}
