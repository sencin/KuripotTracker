package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
}

