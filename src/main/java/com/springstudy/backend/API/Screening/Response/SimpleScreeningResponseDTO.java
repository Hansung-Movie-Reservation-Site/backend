package com.springstudy.backend.API.Screening.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SimpleScreeningResponseDTO {
    private Integer tmdbMovieId;
    private String kobisMovieCd;
    private List<Long> screeningIds;
    private List<Long> roomIds;
    private List<LocalTime> startTimes;
}
