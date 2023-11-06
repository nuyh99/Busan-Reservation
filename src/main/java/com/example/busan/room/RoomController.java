package com.example.busan.room;

import com.example.busan.auth.dto.Authentication;
import com.example.busan.room.dto.RoomResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.example.busan.auth.AuthController.AUTHORIZATION;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(final RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> findAll(@RequestParam(value = "date", required = false) final LocalDate date,
                                                      final HttpSession httpSession) {
        final Authentication authentication = (Authentication) httpSession.getAttribute(AUTHORIZATION);
        final List<RoomResponse> response = roomService.findAllAtDate(date, authentication);
        return ResponseEntity.ok(response);
    }
}
