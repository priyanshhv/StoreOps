package com.laundry.version_one.machine;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MachineRequest(
        @NotNull
        Double averageCostPerHour,
        @NotNull
        @NotEmpty
        String type
) {
}
