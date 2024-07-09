package com.laundry.version_one.booking;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Integer id;
    private String userName;
    private Integer machineId;
    private LocalDate createdDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double bill;
}
