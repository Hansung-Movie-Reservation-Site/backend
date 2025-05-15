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
                new SpotSeed("강남", "서울"), new SpotSeed("건대입구", "서울"), new SpotSeed("대학로", "서울"), new SpotSeed("미아", "서울"),
                new SpotSeed("남양주", "경기"), new SpotSeed("동탄", "경기"), new SpotSeed("분당", "경기"), new SpotSeed("수원", "경기"),
                new SpotSeed("검단", "인천"), new SpotSeed("송도", "인천"), new SpotSeed("영종", "인천"), new SpotSeed("인천논현", "인천"),
                new SpotSeed("남춘천", "강원"), new SpotSeed("속초", "강원"), new SpotSeed("원주혁신", "강원"), new SpotSeed("춘천석사", "강원"),
                new SpotSeed("대구신세계", "대구"), new SpotSeed("대구이시아", "대구"), new SpotSeed("마산", "대구"), new SpotSeed("창원", "대구"),
                new SpotSeed("경상대", "부산"), new SpotSeed("덕천", "부산"), new SpotSeed("부산대", "부산"), new SpotSeed("해운대", "부산"),
                new SpotSeed("서귀포", "제주"), new SpotSeed("제주삼화", "제주"), new SpotSeed("제주아라", "제주")
        );

        int insertedCount = 0;

        for (SpotSeed seed : requiredSpots) {
            boolean exists = spotRepository.existsByNameAndRegion_Name(seed.name(), seed.regionName());
            if (!exists) {
                Region region = regionRepository.findByName(seed.regionName())
                        .orElseThrow(() -> new IllegalArgumentException("❌ Region name not found: " + seed.regionName()));

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
