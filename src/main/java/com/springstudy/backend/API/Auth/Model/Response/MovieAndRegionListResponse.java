package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Movie;
import com.springstudy.backend.API.Repository.Entity.Region;

import java.util.List;

public class MovieAndRegionListResponse {
    private List<Movie> movies;
    private List<Region> regions;
}
