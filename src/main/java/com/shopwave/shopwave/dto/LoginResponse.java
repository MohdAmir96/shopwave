
package com.shopwave.shopwave.dto;

// Defines the data returned to the client after successful login.
public record LoginResponse(
        String accessToken,
        String tokenType
) {}
