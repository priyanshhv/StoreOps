package com.laundry.version_one.store;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record StoreRequest(
        @NotNull
        @NotEmpty
        String name,
        @NotNull
        @NotEmpty
        String address
) {
}
