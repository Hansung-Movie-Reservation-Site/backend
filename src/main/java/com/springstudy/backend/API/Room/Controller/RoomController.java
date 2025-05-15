package com.springstudy.backend.API.Room.Controller;

import com.springstudy.backend.API.Repository.Entity.Room;
import com.springstudy.backend.API.Room.Service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestParam Long spotId) {
        Room room = roomService.createRoom(spotId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/create/multiple")
    public ResponseEntity<?> createMultipleRooms(@RequestParam Long spotId, @RequestParam int count) {
        List<Room> rooms = roomService.createMultipleRooms(spotId, count);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/create/with-seats")
    public ResponseEntity<?> createRoomsWithSeats(@RequestParam Long spotId, @RequestParam int count) {
        List<Room> rooms = roomService.createMultipleRoomsWithSeats(spotId, count);
        return ResponseEntity.ok(rooms);
    }

    /**
     * ✅ 모든 Spot에 Room을 count만큼 생성하고 좌석도 함께 생성
     * POST /api/v1/rooms/create/with-seats-all?count=3
     */
    @PostMapping("/create/with-seats-all")
    public ResponseEntity<List<Room>> createAllRoomsWithSeats(@RequestParam int count) {
        List<Room> rooms = roomService.createAllRoomsWithSeats(count);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/create/with-seats-all-max-5")
    public ResponseEntity<List<Room>> createAllRoomsWithSeatsV2(@RequestParam int count) {
        List<Room> rooms = roomService.createAllRoomsWithSeatsV2(count);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/getAll")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }


}
