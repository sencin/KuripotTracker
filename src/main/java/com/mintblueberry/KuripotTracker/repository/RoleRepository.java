package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByErole(Role.ERole erole);
}
