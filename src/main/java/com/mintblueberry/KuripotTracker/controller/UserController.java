package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")

public class UserController {
    private final UserService userService;

    @GetMapping("/verify-token")
    public ResponseEntity<LinkedHashMap<String, Object>> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
            errorResp.put("success", false);
            errorResp.put("message", "JWT is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
        }
        // Delegate to service
        LinkedHashMap<String, Object> profile = userService.getUserProfileRaw(authentication.getName());
        profile.put("success", true);
        profile.put("message", "JWT is valid");

        return ResponseEntity.ok(profile);
    }
}
