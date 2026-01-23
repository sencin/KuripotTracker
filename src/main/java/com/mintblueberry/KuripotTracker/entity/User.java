package com.mintblueberry.KuripotTracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String extensionName;
    @Column(unique = true)
    private String email;
    private String password;

    private boolean isVerified = false;          // Track if email is verified
    private String verificationCode;             // Store numeric OTP
    private LocalDateTime verificationExpiry;    // Optional: OTP expiry timestamp

    @ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "account_roles",
        joinColumns = @JoinColumn(name = "account_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
