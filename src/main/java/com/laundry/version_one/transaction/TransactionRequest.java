package com.laundry.version_one.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TransactionRequest(
        @NotNull
        Integer bookingId
) {
}
