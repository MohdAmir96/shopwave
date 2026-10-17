
package com.shopwave.shopwave.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedRoute(Authentication authentication) {

        // Spring Security provides the authenticated user's identity.
        return "Hello " + authentication.getName()
                + "! Your JWT authentication is working.";
    }
}
