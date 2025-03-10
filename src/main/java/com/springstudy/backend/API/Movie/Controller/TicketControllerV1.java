package com.springstudy.backend.API.Movie.Controller;

import com.springstudy.backend.API.Movie.Service.TicketService;
import com.springstudy.backend.API.Repository.Entity.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketControllerV1 {

    private final TicketService ticketService;

    public TicketControllerV1(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * ✅ Ticket 생성 API
     * POST /api/v1/tickets
     */
    @PostMapping
    public ResponseEntity<List<Ticket>> createTickets(
            @RequestParam Long screeningId,
            @RequestParam List<Long> seatIds,
            @RequestParam Long userId) {
        try {
            List<Ticket> tickets = ticketService.createTickets(screeningId, seatIds, userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
