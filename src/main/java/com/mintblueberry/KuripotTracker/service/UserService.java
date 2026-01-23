package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.SignupRequest;
import com.mintblueberry.KuripotTracker.entity.Role;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.RoleRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;

    public void registerAccount(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EntityExistsException("Email already used");
        }

        // Get ROLE_USER, create if missing
        Role role = roleRepository.findByErole(Role.ERole.ROLE_USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setErole(Role.ERole.ROLE_USER);
                    return roleRepository.save(newRole);
                });


        String otp = otpService.generateNumericOtp(6);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        User user = new User();
        user.setUsername(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setMiddleName(signupRequest.getMiddleName());
        user.setLastName(signupRequest.getLastName());
        user.setExtensionName(signupRequest.getExtensionName());
        user.setEmail(signupRequest.getEmail());
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
        emailService.sendOtpEmail(signupRequest.getEmail(), otp);
    }

    public LinkedHashMap<String, Object> getUserProfileRaw(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", user.getId());
        resp.put("firstName", user.getFirstName());
        resp.put("lastName", user.getLastName());
        resp.put("role", user.getRoles().stream()
                .map(r -> r.getErole().name())
                .toList());

        return resp;
    }

    @Transactional
    public void deleteUser(Long targetUserId, String token) {
        Long loggedInUserId = jwtService.extractUserId(token);

        if (!targetUserId.equals(loggedInUserId)) {
            throw new RuntimeException("You can only delete your own account");
        }

        User user = userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().clear();

        userRepository.delete(user);
    }

}

