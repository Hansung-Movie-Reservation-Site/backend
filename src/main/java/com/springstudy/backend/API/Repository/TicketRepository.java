package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Screening;
import com.springstudy.backend.API.Repository.Entity.Seat;
import com.springstudy.backend.API.Repository.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // ✅ 특정 Seat이 Ticket 테이블에 존재하는지 확인 (이미 예약된 좌석인지 여부)
    boolean existsBySeat(Seat seat);

    // ✅ 특정 Screening과 Seat이 이미 존재하는지 확인 (중복 방지)
    boolean existsByScreeningAndSeat(Screening screening, Seat seat);
}