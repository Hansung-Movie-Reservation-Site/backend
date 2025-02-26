package com.springstudy.backend.API.Auth.Model;

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

    @JsonProperty("movieCd")
    private String movieCode;

    @JsonProperty("movieNm")
    private String title;

    @JsonProperty("openDt")
    private String releaseDate;

    public LocalDate getParsedReleaseDate() {
        if (releaseDate == null || releaseDate.isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(releaseDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("❌ 날짜 변환 실패: " + releaseDate);
            return null;
        }
    }
}