package com.springstudy.backend.API.Region;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springstudy.backend.API.Repository.Entity.Region;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/getAll")
    public List<Region> getAllRegions() {
        return regionService.getAllRegions();
    }
}
