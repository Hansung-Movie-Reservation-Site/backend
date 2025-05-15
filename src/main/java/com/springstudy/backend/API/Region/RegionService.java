package com.springstudy.backend.API.Region;

import com.springstudy.backend.API.Repository.Entity.Region;
import com.springstudy.backend.API.Repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Transactional
    public void insertMissingRegions() {
        List<String> requiredRegionNames = List.of(
                "서울", "경기", "인천", "강원", "대구", "부산", "제주"
        );

        int insertedCount = 0;

        for (String regionName : requiredRegionNames) {
            boolean exists = regionRepository.existsByName(regionName);
            if (!exists) {
                Region region = Region.builder()
                        .name(regionName)
                        .build();
                regionRepository.save(region);
                insertedCount++;
            }
        }

        System.out.printf("[✅] Region 누락된 데이터 %d개 삽입 완료%n", insertedCount);
    }
}