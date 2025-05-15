package com.springstudy.backend.API.Spot;

import com.springstudy.backend.API.Repository.Entity.Spot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    @GetMapping("/getAll")
    public List<Spot> getAllSpots() {
        return spotService.getAllSpots();
    }
}