package com.mintblueberry.KuripotTracker.repository;

import com.mintblueberry.KuripotTracker.dto.ExpenseCategorySummary;
import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> findByUserId(Long userId);
    Optional<ExpenseCategory> findByIdAndUserId(Long id, Long userId);
    List<ExpenseCategory> findAllByUserId(Long userId);

    @Query("""
    SELECT new com.mintblueberry.KuripotTracker.dto.ExpenseCategorySummary(
        ec.id,
        ec.name,
        ec.image,
        COALESCE(SUM(t.amount), 0)
    )
    FROM ExpenseCategory ec
    LEFT JOIN Transaction t 
        ON t.expenseCategory.id = ec.id AND t.type = 'EXPENSE'
    WHERE ec.user.id = :userId
    GROUP BY ec.id, ec.name, ec.image
""")
    List<ExpenseCategorySummary> getCategorySummaries(@Param("userId") Long userId);
}

