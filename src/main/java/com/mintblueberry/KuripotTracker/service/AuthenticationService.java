package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.LoginRequest;
import com.mintblueberry.KuripotTracker.dto.SignupRequest;
import com.mintblueberry.KuripotTracker.entity.Role;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.RoleRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepository,  JwtService jwtService, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }
    public String login(LoginRequest loginRequest) {
        // Create authentication token
        Authentication authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword());

        // Authenticate the user
        Authentication authResponse = authenticationManager.authenticate(authRequest);

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authResponse);

        // Generate and return JWT for Bearer usage
        return jwtService.generateToken(loginRequest.getEmail());
    }


    public void registerAccount(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EntityExistsException("Email already used");
        }

        Role role = roleRepository.findByErole(Role.ERole.ROLE_USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setErole(Role.ERole.ROLE_USER);
                    return roleRepository.save(newRole);
                });

        User user = new User();
        user.setUsername(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setMiddleName(signupRequest.getMiddleName());
        user.setLastName(signupRequest.getLastName());
        user.setExtensionName(signupRequest.getExtensionName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        user.setRoles(Set.of(role));

        userRepository.save(user);
    }
    public void logoutUser(HttpServletResponse response){

    }
}