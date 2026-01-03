package com.mintblueberry.KuripotTracker.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Data
@NoArgsConstructor

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole erole = ERole.ROLE_USER;

    public Role(ERole erole) {
        this.erole = erole;
    }

    public enum ERole {
        ROLE_USER,
        ROLE_ADMIN
    }

}

