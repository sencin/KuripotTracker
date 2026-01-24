package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.config.CustomUserDetails;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepository,  JwtService jwtService, RoleRepository roleRepository, OtpService otpService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.otpService = otpService;
        this.emailService = emailService;
    }
    public String login(LoginRequest loginRequest) {
        // Create authentication token
        Authentication authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword());

        // Authenticate the user
        Authentication authResponse = authenticationManager.authenticate(authRequest);


        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authResponse);


        Object principal = authResponse.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Authenticated principal is not a valid user");
        }

        // Safely get the user from CustomUserDetails
        User user = Objects.requireNonNull(userDetails.getUser(), "Authenticated user cannot be null");

        // Generate and return JWT using the full User
        return jwtService.generateToken(user);
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

        String otp = otpService.generateNumericOtp(6);

        User user = new User();
        user.setUsername(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setMiddleName(signupRequest.getMiddleName());
        user.setLastName(signupRequest.getLastName());
        user.setExtensionName(signupRequest.getExtensionName());
        user.setEmail(signupRequest.getEmail());
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setVerificationCode(otp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(5));
        user.setRoles(Set.of(role));

        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }
    public void logoutUser(HttpServletResponse response){

    }

    public void confirmOtp(String email, String otp) {
        otpService.verifyOtp(email, otp);
    }

    //delete this after testing website
    public void sendEmailToAdmin(String message){
        emailService.sendEmailAdmin("smtp2go.faceplate463@passinbox.com",message);
    }
}