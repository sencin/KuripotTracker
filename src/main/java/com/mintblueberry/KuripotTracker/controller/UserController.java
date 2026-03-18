package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.service.AuthenticationService;
import com.mintblueberry.KuripotTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")

public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyUserToken(Authentication authentication) {

        boolean isValid = authenticationService.verifyUserToken(authentication);

        if (!isValid) {
            LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
            errorResp.put("message", "JWT is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
        }

        LinkedHashMap<String, Object> successResp = new LinkedHashMap<>();
        successResp.put("message", "JWT is valid");
        return ResponseEntity.ok(successResp);
    }

    @GetMapping("/account")
    public ResponseEntity<LinkedHashMap<String, Object>> displayUserCredentials(Authentication authentication){

        boolean isValid = authenticationService.verifyUserToken(authentication);

        if (!isValid) {
            LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
            errorResp.put("message", "JWT is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
        }

        LinkedHashMap<String, Object> profile = userService.retrieveUserInformation(authentication.getName());
        return ResponseEntity.ok(profile);
    }
}
