package com.laundry.version_one.booking;

import com.laundry.version_one.common.PageResponse;
import com.laundry.version_one.machine.MachineResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("booking")
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {
    private final BookingService service;
    //find the available slots for a machine at least 10 minutes
    @GetMapping("/{machine_id}/availableSlots")
    public ResponseEntity<List<BookingSlotResponse>> findAvailableSlots(@PathVariable("machine_id") Integer machineId) {
        return ResponseEntity.ok(service.findAvailableSlots(machineId));
    }

    //save the booking of a user if its valid slot for current day
    @PostMapping("/{machine_id}")
    public ResponseEntity<Integer> saveBooking(
            @PathVariable("machine_id") Integer machineId,
            @Valid @RequestBody BookingRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(machineId,request,connectedUser));
    }

    //find the bookings for a given machine
    @GetMapping("/{machine_id}")
    public ResponseEntity<PageResponse<BookingResponse>> findBooking(
            @PathVariable("machine_id") Integer machineId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
            ) {
        return ResponseEntity.ok(service.findAllBookingsOfMachine(machineId,page,size));
    }
}
