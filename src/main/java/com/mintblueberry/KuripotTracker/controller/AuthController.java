package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.LoginRequest;
import com.mintblueberry.KuripotTracker.dto.SignupRequest;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import com.mintblueberry.KuripotTracker.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

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
            e.printStackTrace();
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            authenticationService.confirmOtp(email, otp); // void method
            response.put("success", "true");
            response.put("message", "OTP verified successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LinkedHashMap<String, Object>> login(@RequestBody LoginRequest request) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try {
            String token = authenticationService.login(request);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            resp.put("success", true);
            resp.put("token", token);
            resp.put("id", user.getId());
            resp.put("firstName", user.getFirstName());
            resp.put("lastName", user.getLastName());

            resp.put("role", user.getRoles().stream()
                    .map(r -> r.getErole().name())
                    .toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LinkedHashMap<String, Object>> logout() {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "User logged out. Please remove the token on client side.");
        return ResponseEntity.ok(resp);
    }

}

