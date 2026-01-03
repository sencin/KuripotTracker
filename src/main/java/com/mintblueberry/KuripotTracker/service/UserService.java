package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.SignupRequest;
import com.mintblueberry.KuripotTracker.entity.Role;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.RoleRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
}

