package com.springstudy.backend.API.Auth.Model.Response;

import com.springstudy.backend.API.Repository.Entity.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegionListResponse {
    private List<Region> regions;
}
