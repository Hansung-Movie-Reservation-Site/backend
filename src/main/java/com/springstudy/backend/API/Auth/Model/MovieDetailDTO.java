package com.springstudy.backend.API.Auth.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailDTO {

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("runtime")
    private Integer runtime;  // ✅ 상영 시간 추가

    @JsonProperty("genres")
    private List<GenreDTO> genres;

    @JsonProperty("credits")
    private CreditsDTO credits;

    public String getFullPosterUrl() {
        return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
    }

    public String getGenreNames() {
        return (genres != null && !genres.isEmpty())
                ? genres.stream().map(GenreDTO::getName).collect(Collectors.joining(", "))
                : null;
    }

    public String getDirectorNames() {
        return (credits != null && credits.getDirectors() != null && !credits.getDirectors().isEmpty())
                ? credits.getDirectors().stream().map(CrewDTO::getName).collect(Collectors.joining(", "))
                : null;
    }
}

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class GenreDTO {
    @JsonProperty("name")
    private String name;
}

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class CreditsDTO {
    @JsonProperty("crew")
    private List<CrewDTO> directors;

    public List<CrewDTO> getDirectors() {
        return (directors != null) ? directors.stream()
                .filter(crew -> "Director".equals(crew.getJob()) && crew.getName() != null && !crew.getName().trim().isEmpty())
                .collect(Collectors.toList()) : null;
    }
}

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class CrewDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("job")  // ✅ 감독인지 배우인지 구별
    private String job;
}