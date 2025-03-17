package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Screening;
import com.springstudy.backend.API.Repository.Entity.Seat;
import com.springstudy.backend.API.Repository.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ✅ 특정 Seat이 Ticket 테이블에 존재하는지 확인 (이미 예약된 좌석인지 여부)
    // boolean existsBySeat(Seat seat);

    // ✅ 특정 Seat가 예약되었는지 확인 (order가 null이 아닌 경우)
    boolean existsBySeatAndOrderIsNotNull(Seat seat);

    // ✅ 특정 Screening과 Seat이 이미 존재하는지 확인 (중복 방지)
    boolean existsByScreeningAndSeat(Screening screening, Seat seat);

    /**
     * ✅ 특정 좌석 ID 리스트가 특정 상태(PENDING, PAID)의 주문에 포함되어 있는지 확인
     */
    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.seat.id IN :seatIds AND t.order.status IN :statuses")
    boolean existsBySeatIdsAndOrderStatuses(@Param("seatIds") List<Long> seatIds,
                                            @Param("statuses") List<String> statuses);

    Ticket findByScreeningAndSeat(Screening screening, Seat seat); // ✅ 기존 티켓 조회 메소드 추가
}