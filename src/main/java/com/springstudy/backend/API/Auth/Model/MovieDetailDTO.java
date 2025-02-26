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

    @JsonProperty("movieCd")
    private String movieCode;

    @JsonProperty("directors")
    private List<DirectorDTO> directors;

    @JsonProperty("actors")
    private List<ActorDTO> actors;

    public String getDirectorNames() {
        return directors.stream().map(DirectorDTO::getName).collect(Collectors.joining(", "));
    }

    public String getActorNames() {
        return actors.stream().map(ActorDTO::getName).collect(Collectors.joining(", "));
    }
}

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class DirectorDTO {
    @JsonProperty("peopleNm")
    private String name;
}

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class ActorDTO {
    @JsonProperty("peopleNm")
    private String name;
}
