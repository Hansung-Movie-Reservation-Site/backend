package com.springstudy.backend.API.Spot;

import com.springstudy.backend.API.Repository.Entity.Region;
import com.springstudy.backend.API.Repository.Entity.Spot;
import com.springstudy.backend.API.Repository.RegionRepository;
import com.springstudy.backend.API.Repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotService {

    private final SpotRepository spotRepository;
    private final RegionRepository regionRepository;

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    @Transactional
    public void insertMissingSpots() {
        List<SpotSeed> requiredSpots = List.of(
                new SpotSeed("강남", 1L), new SpotSeed("건대입구", 1L), new SpotSeed("대학로", 1L), new SpotSeed("미아", 1L),
                new SpotSeed("남양주", 2L), new SpotSeed("동탄", 2L), new SpotSeed("분당", 2L), new SpotSeed("수원", 2L),
                new SpotSeed("검단", 3L), new SpotSeed("송도", 3L), new SpotSeed("영종", 3L), new SpotSeed("인천논현", 3L),
                new SpotSeed("남춘천", 4L), new SpotSeed("속초", 4L), new SpotSeed("원주혁신", 4L), new SpotSeed("춘천석사", 4L),
                new SpotSeed("대구신세계", 5L), new SpotSeed("대구이시아", 5L), new SpotSeed("마산", 5L), new SpotSeed("창원", 5L),
                new SpotSeed("경상대", 6L), new SpotSeed("덕천", 6L), new SpotSeed("부산대", 6L), new SpotSeed("해운대", 6L),
                new SpotSeed("서귀포", 7L), new SpotSeed("제주삼화", 7L), new SpotSeed("제주아라", 7L)
        );

        int insertedCount = 0;

        for (SpotSeed seed : requiredSpots) {
            boolean exists = spotRepository.existsByNameAndRegionId(seed.name(), seed.regionId());
            if (!exists) {
                Region region = regionRepository.getReferenceById(seed.regionId());
                Spot spot = Spot.builder()
                        .name(seed.name())
                        .region(region)
                        .build();
                spotRepository.save(spot);
                insertedCount++;
            }
        }

        System.out.printf("[✅] Spot 누락된 데이터 %d개 삽입 완료%n", insertedCount);
    }
}
