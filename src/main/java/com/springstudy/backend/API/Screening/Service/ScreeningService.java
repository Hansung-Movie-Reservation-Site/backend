package com.springstudy.backend.API.Screening.Service;

import com.springstudy.backend.API.Movie.Model.Response.MovieResponseDTO;
import com.springstudy.backend.API.Movie.Model.Response.MovieResponseIdDTO;
import com.springstudy.backend.API.Screening.Response.ScreeningAddDTO;
import com.springstudy.backend.API.Screening.Response.SeatResponseDTO;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.*;
import com.springstudy.backend.API.Screening.Response.SimpleScreeningResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScreeningService {

    private final SpotRepository spotRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;


    public ScreeningService(SpotRepository spotRepository, RoomRepository roomRepository, ScreeningRepository screeningRepository, SeatRepository seatRepository, TicketRepository ticketRepository, MovieRepository movieRepository) {
        this.spotRepository = spotRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.movieRepository = movieRepository;
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

    @Transactional(readOnly = true)
    public List<SimpleScreeningResponseDTO> getSimplifiedScreeningsBySpotDateAndMovieId(String spotName, LocalDate date, Long movieId) {

        // spot 조회
        Long spotId = spotRepository.findByName(spotName)
                .map(Spot::getId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 Spot이 존재하지 않습니다: " + spotName));

        // room 조회
        List<Long> roomIds = roomRepository.findBySpotId(spotId)
                .stream()
                .map(Room::getId)
                .toList();

        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("❌ 해당 Spot에 등록된 Room이 없습니다.");
        }

        List<Screening> screenings = screeningRepository.findByRoomIdInAndDateAndMovieId(roomIds, date, movieId);

        if (screenings.isEmpty()) {
            return List.of();
        }

        // 같은 영화이므로 하나만 가져도 됨
        Movie movie = screenings.get(0).getMovie();

        return List.of(
                SimpleScreeningResponseDTO.builder()
                        .tmdbMovieId(movie.getTmdbMovieId())
                        .kobisMovieCd(movie.getKobisMovieCd())
                        .screeningIds(screenings.stream().map(Screening::getId).toList())
                        .roomIds(screenings.stream().map(s -> s.getRoom().getId()).toList())
                        .startTimes(screenings.stream().map(Screening::getStart).toList())  // ✅ 추가
                        .build()
        );
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


    /**
     * 코드 작성 중, 에러 발생
     */

//    @Transactional(readOnly = true)
//    public Map<String, List<ScreeningAddDTO>> generateScreeningsGroupedByDay(LocalDate startDate, int count, int price) {
//        Map<String, List<ScreeningAddDTO>> grouped = new LinkedHashMap<>();
//
//
//        LocalDate currentDate = startDate.plusDays(0);
//
//        List<ScreeningAddDTO> dtoList = generateScreeningsForDate(currentDate, count, price);
//
//        dtoList.sort(
//                    Comparator
//                            .comparing(ScreeningAddDTO::getRoomNumber) // 1️⃣ 방 번호 기준 정렬
//                            .thenComparing(ScreeningAddDTO::getStart)  // 2️⃣ 같은 방이면 상영 시작 시간 정렬
//        );
//
//        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
//
//        grouped.put(dayOfWeek, dtoList);
//
//
//        return grouped;
//    }
//
//    public Map<String, List<ScreeningAddDTO>> generateScreeningsGroupedByDayOfWeek(LocalDate startDate, int count, int price) {
//        Map<String, List<ScreeningAddDTO>> grouped = new LinkedHashMap<>();
//
//        for (int i = 0; i < 7; i++) {
//            LocalDate currentDate = startDate.plusDays(i);
//
//            List<ScreeningAddDTO> dtoList = generateScreeningsForDate(currentDate, count, price);
//
//            dtoList.sort(
//                    Comparator
//                            .comparing(ScreeningAddDTO::getRoomNumber) // 1️⃣ 방 번호 기준 정렬
//                            .thenComparing(ScreeningAddDTO::getStart)  // 2️⃣ 같은 방이면 상영 시작 시간 정렬
//            );
//
//            String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
//
//            grouped.put(dayOfWeek, dtoList);
//        }
//
//        return grouped;
//    }
//
//
//    @Transactional
//    public List<ScreeningAddDTO> generateScreeningsForDate(LocalDate date, int count, int price) {
//
//        List<Movie> movies = movieRepository.findAll();
//        List<Room> rooms = roomRepository.findAll();
//        Random random = new Random();
//        List<ScreeningAddDTO> createdDTOs = new ArrayList<>();
//
//        LocalTime startTime = LocalTime.of(9, 0);
//        LocalTime endTime = LocalTime.of(23, 59);
//
//        for (Room room : rooms) {
//            List<LocalTime[]> timeSlots = new ArrayList<>();
//
//            // LocalTime roomStartTime = LocalTime.of(9, 0);
//            // LocalTime roomEndTime = LocalTime.of(23, 59);
//
//            int generated = 0;
//
//            while (generated < count) {
//
//                Movie movie = movies.get(random.nextInt(movies.size()));
//                int runtime = movie.getRuntime() != null ? movie.getRuntime() : 100;
//                int buffer = 30;
//
//                // 랜덤 시작 시간
//                int availableMinutes = (int) Duration.between(startTime, endTime).toMinutes() - runtime - buffer;
//
//                if (availableMinutes <= 0) break;
//
//                int randomMinutes = random.nextInt(availableMinutes);
//
//                LocalTime potentialStart = startTime.plusMinutes(randomMinutes);
//
//                // 5분 단위로 정렬
//                int startMod = potentialStart.getMinute() % 5;
//                final LocalTime adjustedStart = potentialStart.plusMinutes(startMod == 0 ? 0 : 5 - startMod);
//                LocalTime potentialFinish = adjustedStart.plusMinutes(runtime + buffer);
//                int finishMod = potentialFinish.getMinute() % 5;
//                final LocalTime adjustedFinish = potentialFinish.plusMinutes(finishMod == 0 ? 0 : 5 - finishMod);
//
//                if (adjustedFinish.isAfter(endTime)) continue;
//
//                // timeSlot 정렬 후 겹침 확인
//                timeSlots.sort(Comparator.comparing(slot -> slot[0]));
//
//                boolean overlaps = timeSlots.stream().anyMatch(slot ->
//                        !(adjustedFinish.isBefore(slot[0]) || adjustedStart.isAfter(slot[1]))
//                );
//                if (overlaps) continue;
//
//                timeSlots.add(new LocalTime[]{adjustedStart, adjustedFinish});
//                // roomStartTime = adjustedFinish;
//
//
//                // Screening 저장
//                Screening screening = Screening.builder()
//                        .movie(movie)
//                        .room(room)
//                        .date(date)
//                        .start(adjustedStart)
//                        .finish(adjustedFinish)
//                        .price(price)
//                        .build();
//
//                System.out.println("8$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                System.out.println("date : " + screening.getDate());
//                System.out.println("room : " + screening.getRoom());
//                System.out.println("start : " + screening.getStart());
//                System.out.println("finish : " + screening.getFinish());
//                System.out.println("generated : " + generated);
//                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//
//                screeningRepository.save(screening);
//
//                // DTO 반환용
//                ScreeningAddDTO dto = new ScreeningAddDTO(
//                        date,
//                        room.getRoomnumber(),
//                        movie.getTitle(),
//                        adjustedStart,
//                        adjustedFinish
//                );
//                createdDTOs.add(dto);
//                generated++;
//
//
//
//                System.out.println("----------------------------------------");
//                System.out.println("date : " + dto.getDate());
//                System.out.println("room : " + dto.getRoomNumber());
//                System.out.println("start : " + dto.getStart());
//                System.out.println("finish : " + dto.getFinish());
//                System.out.println("generated : " + generated);
//                System.out.println("---------------------------------------");
//            }
//
//        }
//
//        return createdDTOs;
//    }

}