package com.springstudy.backend.API.Spot;

import com.springstudy.backend.API.Repository.Entity.Spot;
import com.springstudy.backend.API.Repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotService {

    private final SpotRepository spotRepository;

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }
}
