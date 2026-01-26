package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    List<PaymentType> findAllByUserId(Long userId);

    Optional<PaymentType> findByIdAndUserId(Long id, Long userId);
}
