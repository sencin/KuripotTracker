package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
}
