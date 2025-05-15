package com.springstudy.backend.API.Room.Service;

import com.springstudy.backend.API.Repository.Entity.Room;
import com.springstudy.backend.API.Repository.Entity.Seat;
import com.springstudy.backend.API.Repository.Entity.Spot;
import com.springstudy.backend.API.Repository.RoomRepository;
import com.springstudy.backend.API.Repository.SeatRepository;
import com.springstudy.backend.API.Repository.SpotRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final SpotRepository spotRepository;
    private final SeatRepository seatRepository;

    public RoomService(RoomRepository roomRepository, SpotRepository spotRepository, SeatRepository seatRepository) {
        this.roomRepository = roomRepository;
        this.spotRepository = spotRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * ✅ 특정 spotId에 대해 roomnumber를 자동 증가시켜 저장
     */
    @Transactional
    public Room createRoom(Long spotId) {
        Optional<Spot> optionalSpot = spotRepository.findById(spotId);

        if (optionalSpot.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다. ID: " + spotId);
        }

        Spot spot = optionalSpot.get();

        // ✅ 해당 Spot에 존재하는 Room의 최대 번호 조회
        Long maxRoomNumber = roomRepository.findMaxRoomNumberBySpotId(spotId).orElse(0L);
        Long newRoomNumber = maxRoomNumber + 1;

        Room room = Room.builder()
                .spot(spot)
                .roomnumber(newRoomNumber)
                .build();

        return roomRepository.save(room);
    }

    @Transactional
    public List<Room> createMultipleRooms(Long spotId, int count) {
        Optional<Spot> optionalSpot = spotRepository.findById(spotId);

        if (optionalSpot.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다. ID: " + spotId);
        }

        Spot spot = optionalSpot.get();

        // 기존 Room 중 가장 큰 roomnumber 가져오기
        Long currentMax = roomRepository.findMaxRoomNumberBySpotId(spotId).orElse(0L);

        List<Room> newRooms = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Room room = Room.builder()
                    .spot(spot)
                    .roomnumber(currentMax + i)
                    .build();
            newRooms.add(room);
        }

        return roomRepository.saveAll(newRooms);
    }

    /**
     * spotid에 room을 count만큼 추가
     * 각 room당 a1 ~ j10만큼 좌석 추가
     * @param spotId
     * @param count
     * @return
     */
    @Transactional
    public List<Room> createMultipleRoomsWithSeats(Long spotId, int count) {
        Optional<Spot> optionalSpot = spotRepository.findById(spotId);
        if (optionalSpot.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다. ID: " + spotId);
        }

        Spot spot = optionalSpot.get();

        /**
         * 현재 최대 roomnumber 반환, 없으면 0
         */
        Long currentMax = roomRepository.findMaxRoomNumberBySpotId(spotId).orElse(0L);

        List<Room> createdRooms = new ArrayList<>();

        /**
         * 현재 roomnumber 최대 + 1 ~ 최대 + count
         */
        for (int i = 1; i <= count; i++) {
            Room room = Room.builder()
                    .spot(spot)
                    .roomnumber(currentMax + i)
                    .build();

            Room savedRoom = roomRepository.save(room); // 먼저 Room 저장

            List<Seat> seats = generateSeatsForRoom(savedRoom); // 좌석 생성
            seatRepository.saveAll(seats); // 좌석 저장

            createdRooms.add(savedRoom);
        }

        return createdRooms;
    }

    /**
     * 모든 spot에 room을 count만큼 추가
     * 각 room당 a1 ~ j10만큼 좌석 추가
     * @param count
     * @return
     */
    @Transactional
    public List<Room> createAllRoomsWithSeats(int count) {
        List<Spot> allSpots = spotRepository.findAll();
        List<Room> createdRooms = new ArrayList<>();

        for (Spot spot : allSpots) {
            Long spotId = spot.getId();

            // 현재 최대 roomnumber 조회
            Long currentMax = roomRepository.findMaxRoomNumberBySpotId(spotId).orElse(0L);

            for (int i = 1; i <= count; i++) {
                Room room = Room.builder()
                        .spot(spot)
                        .roomnumber(currentMax + i)
                        .build();

                Room savedRoom = roomRepository.save(room);

                List<Seat> seats = generateSeatsForRoom(savedRoom);
                seatRepository.saveAll(seats);

                createdRooms.add(savedRoom);
            }
        }

        return createdRooms;
    }

    @Transactional
    public List<Room> createAllRoomsWithSeatsV2(int count) {
        List<Spot> allSpots = spotRepository.findAll();
        List<Room> createdRooms = new ArrayList<>();

        for (Spot spot : allSpots) {
            Long spotId = spot.getId();

            // 현재 spot에 존재하는 room 수 조회
            int existingRoomCount = roomRepository.countBySpotId(spotId);
            // System.out.println("spot Id: " + spotId + " existingRoomCount: " + existingRoomCount);

            int roomsToCreate = Math.min(count, 5 - existingRoomCount);

            if (roomsToCreate <= 0) continue;

            // 현재 최대 roomnumber 조회
            Long currentMax = roomRepository.findMaxRoomNumberBySpotId(spotId).orElse(0L);

            for (int i = 1; i <= roomsToCreate; i++) {
                Room room = Room.builder()
                        .spot(spot)
                        .roomnumber(currentMax + i)
                        .build();

                Room savedRoom = roomRepository.save(room);

                List<Seat> seats = generateSeatsForRoom(savedRoom);
                seatRepository.saveAll(seats);

                createdRooms.add(savedRoom);
            }
        }

        return createdRooms;
    }


    private List<Seat> generateSeatsForRoom(Room room) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'a'; row <= 'j'; row++) { // 행: a~j
            for (int col = 1; col <= 10; col++) { // 열: 1~10
                Seat seat = Seat.builder()
                        .room(room)
                        .horizontal(String.valueOf(row))
                        .vertical(col)
                        .build();
                seats.add(seat);
            }
        }

        return seats;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


}
