package com.example.busan.room;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.RoomResponse;
import com.example.busan.room.dto.UpdateRoomRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final CreateRoomRequest request,
                                       @Authorized final Authentication authentication) {
        authentication.validateAdmin();
        roomService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<Void> update(@RequestBody final UpdateRoomRequest request,
                                       @Authorized final Authentication authentication,
                                       @PathVariable("roomId") final Long roomId) {
        authentication.validateAdmin();
        roomService.update(roomId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> delete(@Authorized final Authentication authentication,
                                       @PathVariable("roomId") final Long roomId) {
        authentication.validateAdmin();
        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> findAll(@RequestParam(value = "date") final LocalDate date,
                                                      final HttpSession httpSession) {
        final Authentication authentication = (Authentication) httpSession.getAttribute(AUTHORIZATION);
        final List<RoomResponse> response = roomService.findAllAtDate(date, authentication);
        return ResponseEntity.ok(response);
    }
}
