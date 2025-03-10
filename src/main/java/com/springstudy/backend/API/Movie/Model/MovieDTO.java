package com.springstudy.backend.API.Movie.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {

    @JsonProperty("id")
    private Integer movieId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    public LocalDate getParsedReleaseDate() {
        if (releaseDate == null || releaseDate.isEmpty()) return null;
        try {
            return LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.err.println("❌ 날짜 변환 실패: " + releaseDate);
            return null;
        }
    }
}