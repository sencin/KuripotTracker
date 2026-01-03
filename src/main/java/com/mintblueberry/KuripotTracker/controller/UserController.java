package com.mintblueberry.KuripotTracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<LinkedHashMap<String, Object>> getProfile(Authentication authentication) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            // JWT was valid and SecurityContextHolder has the auth
            resp.put("success", true);
            resp.put("message", "JWT is valid");
            resp.put("user", authentication.getName()); // just return username/email
            resp.put("roles", authentication.getAuthorities()); // return roles
            return ResponseEntity.ok(resp);
        } else {
            resp.put("success", false);
            resp.put("message", "JWT is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }
}
