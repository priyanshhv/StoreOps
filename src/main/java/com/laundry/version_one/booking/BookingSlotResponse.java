package com.laundry.version_one.booking;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingSlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
}
