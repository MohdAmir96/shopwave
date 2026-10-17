
package com.shopwave.shopwave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        // Username must not be empty or contain only whitespace.
        @NotBlank
        @Size(min = 3, max = 50)
        String userName,

        // Password must be provided.
        @NotBlank
        String password

) {}
