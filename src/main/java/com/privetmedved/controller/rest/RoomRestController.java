package com.privetmedved.controller.rest;

import com.privetmedved.entity.Room;
import com.privetmedved.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/rooms")
@Tag(name = "Rooms Public", description = "Public room information API")
public class RoomRestController {

    private final RoomService roomService;

    public RoomRestController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Get all public rooms")
    @GetMapping
    public ResponseEntity<List<Room>> getPublicRooms() {
        return ResponseEntity.ok(roomService.findPublicRooms());
    }

    @Operation(summary = "Get most popular rooms")
    @GetMapping("/popular")
    public ResponseEntity<List<Room>> getPopularRooms() {
        return ResponseEntity.ok(roomService.findMostPopularRooms());
    }
}