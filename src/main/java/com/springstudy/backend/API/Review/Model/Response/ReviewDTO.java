package com.springstudy.backend.API.Review.Model.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewDTO(
        @NotNull
        String user,
        @NotBlank
        Float rate,
        @NotNull
        String review,
        @NotNull
        String title,
        @NotNull
        boolean spoiler,
        @NotNull
        String poster
) {
}
