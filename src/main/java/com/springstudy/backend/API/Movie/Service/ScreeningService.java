package com.springstudy.backend.API.Movie.Service;

import com.springstudy.backend.API.Movie.Model.Response.MovieResponseDTO;
import com.springstudy.backend.API.Movie.Model.Response.MovieResponseIdDTO;
import com.springstudy.backend.API.Movie.Model.Response.SeatResponseDTO;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScreeningService {

    private final SpotRepository spotRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;


    public ScreeningService(SpotRepository spotRepository, RoomRepository roomRepository, ScreeningRepository screeningRepository, SeatRepository seatRepository, TicketRepository ticketRepository) {
        this.spotRepository = spotRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * ✅ 특정 Spot 이름으로 Spot ID 찾기
     */
    private Long findSpotByName(String spotName) {
        return spotRepository.findByName(spotName)
                .map(Spot::getId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다: " + spotName));
    }

    /**
     * ✅ Spot ID로 해당 Spot의 모든 Room ID 조회
     */
    private List<Long> findRoomIdsBySpotId(Long spotId) {
        List<Long> roomIds = roomRepository.findBySpotId(spotId)
                .stream()
                .map(Room::getId)
                .collect(Collectors.toList());

        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot에 등록된 Room이 없습니다.");
        }
        return roomIds;
    }

    /**
     * ✅ 특정 Room ID 리스트와 날짜로 Screening 데이터 조회
     */
    private List<Screening> findScreeningsBySpotAndDate(String spotName, LocalDate date) {
        Long spotId = findSpotByName(spotName);
        List<Long> roomIds = findRoomIdsBySpotId(spotId);
        return screeningRepository.findByRoomIdInAndDate(roomIds, date);
    }

    /**
     * ✅ 특정 날짜와 Spot 이름을 이용하여 Screening 데이터 조회
     */
    @Transactional(readOnly = true)
    public List<Screening> getScreeningsBySpotAndDate(String spotName, LocalDate date) {
        return findScreeningsBySpotAndDate(spotName, date);
    }

    /**
     * ✅ 특정 날짜와 Spot 이름을 이용하여 영화명 리스트 반환 (중복 제거)
     */
    @Transactional(readOnly = true)
    public List<String> getMovieTitlesBySpotAndDate(String spotName, LocalDate date) {
        return findScreeningsBySpotAndDate(spotName, date)
                .stream()
                .map(screening -> screening.getMovie().getTitle())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 날짜와 Spot 이름을 이용하여 영화명과 포스터 URL 반환 (중복 제거)
     */
    @Transactional(readOnly = true)
    public List<MovieResponseDTO> getMoviesBySpotAndDate(String spotName, LocalDate date) {
        Set<MovieResponseDTO> movieSet = new LinkedHashSet<>(findScreeningsBySpotAndDate(spotName, date)
                .stream()
                .map(screening -> new MovieResponseDTO(
                        screening.getMovie().getTitle(),
                        screening.getMovie().getPosterImage()
                ))
                .collect(Collectors.toList()));

        return List.copyOf(movieSet);
    }

    /**
     * ✅ 날짜, Spot 이름, 영화 ID를 이용하여 Screening 데이터 조회
     */
    @Transactional(readOnly = true)
    public List<Screening> getScreeningsBySpotDateAndMovieId(String spotName, LocalDate date, Long movieId) {
        System.out.println("----------------------------------");

        // 1️⃣ Spot name으로 spot ID 찾기
        Optional<Spot> optionalSpot = spotRepository.findByName(spotName);
        if (optionalSpot.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다: " + spotName);
        }
        Long spotId = optionalSpot.get().getId();

        // 2️⃣ Room 테이블에서 해당 spotId를 가진 모든 Room ID 조회
        List<Long> roomIds = roomRepository.findBySpotId(spotId)
                .stream()
                .map(Room::getId)
                .collect(Collectors.toList());

        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot에 등록된 Room이 없습니다.");
        }

        // 3️⃣ Screening 테이블에서 roomId, date, movieId가 일치하는 데이터 조회
        return screeningRepository.findByRoomIdInAndDateAndMovieId(roomIds, date, movieId);
    }

    /**
     * ✅ Screening ID로 Seat 목록 조회 (예약 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getSeatsByScreeningId(Long screeningId) {

        // 1️⃣ Screening ID로 Screening 데이터 조회
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 Screening ID가 존재하지 않습니다: " + screeningId));

        // 2️⃣ Screening 엔티티에서 roomId 조회
        Long roomId = screening.getRoom().getId();

        // 3️⃣ Seat 테이블에서 roomId에 해당하는 모든 좌석 조회
        List<Seat> seats = seatRepository.findByRoomId(roomId);

        // 4️⃣ 좌석 정보에 예약 여부 추가하여 반환
        return seats.stream()
                .map(seat -> new SeatResponseDTO(
                        seat.getId(),
                        seat.getHorizontal(),
                        seat.getVertical(),
                        ticketRepository.existsBySeatAndOrderIsNotNull(seat) // ✅ 예약 여부 확인
                ))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 현재 상영 중인 모든 영화 목록 조회 (movieId 포함)
     */
    @Transactional(readOnly = true)
    public List<MovieResponseIdDTO> getAllScreeningMovies() {
        List<Movie> movies = screeningRepository.findAllMoviesFromScreenings();
        return movies.stream()
                .map(movie -> new MovieResponseIdDTO(
                        movie.getId(),       // ✅ movieId 추가
                        movie.getTitle(),
                        movie.getPosterImage()
                ))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 문자열을 포함하는 영화 제목의 상영 정보 조회 (입력 검증 포함)
     */
    @Transactional(readOnly = true)
    public List<Screening> getScreeningsByMovieTitle(String title) {
        // ✅ 입력 검증: title이 null이거나 공백인지 확인
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ 검색할 영화 제목을 입력해주세요.");
        }

        return screeningRepository.findByMovieTitleContaining(title);
    }
}