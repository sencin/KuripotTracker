package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.LoginRequest;
import com.mintblueberry.KuripotTracker.dto.SignupRequest;
import com.mintblueberry.KuripotTracker.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<LinkedHashMap<String, Object>> signup(@RequestBody SignupRequest signupRequest) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        try {
            authenticationService.registerAccount(signupRequest);
            response.put("message", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityExistsException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LinkedHashMap<String, Object>> login(@RequestBody LoginRequest request) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try {
            String token = authenticationService.login(request);

            resp.put("success", true);
            resp.put("message", request.getEmail() + " signed in");
            resp.put("token", token);

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }


}

