package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Optional: find all transactions by user
    List<Transaction> findByUserId(Long userId);
}

