package com.laundry.version_one.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookingSlotRequest(
        @NotNull
        @NotEmpty
        Integer machineId
) {
}
