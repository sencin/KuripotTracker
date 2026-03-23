package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.ExpenseCategorySummary;
import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.ExpenseCategoryRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // Create a new category for the logged-in user
    @Transactional
    public ExpenseCategory createExpenseCategory(String token, String name, String image) {
        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseCategory category = ExpenseCategory.builder()
                .name(name)
                .user(user)
                .image(image) // can be null
                .build();

        return expenseCategoryRepository.save(category);
    }


    // Get all categories for the logged-in user
    @Transactional(readOnly = true)
    public List<ExpenseCategory> getAllExpenseCategories(String token) {
        Long userId = jwtService.extractUserId(token); // get the logged-in user
        return expenseCategoryRepository.findAllByUserId(userId); // only categories for this user
    }

    // Get category by ID (only if owned by user)
    public ExpenseCategory getExpenseCategoryById(String token, Long categoryId) {
        Long userId = jwtService.extractUserId(token);
        return expenseCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Expense category not found"));
    }

    // Update category name
    @Transactional
    public ExpenseCategory updateExpenseCategory(String token, Long categoryId, String name, String image) {
        Long userId = jwtService.extractUserId(token);

        ExpenseCategory category = expenseCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Expense category not found"));

        // Update name
        if (name != null && !name.isBlank()) {
            category.setName(name);
        }

        // Update image only if provided
        if (image != null) {
            category.setImage(image);
        }

        return expenseCategoryRepository.save(category);
    }

    // Delete category
    @Transactional
    public void deleteExpenseCategory(String token, Long categoryId) {
        Long userId = jwtService.extractUserId(token);
        ExpenseCategory category = expenseCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Expense category not found"));

        expenseCategoryRepository.delete(category);
    }

    public List<ExpenseCategorySummary> getCategorySummaries(String token) {
        Long userId = jwtService.extractUserId(token);
        return expenseCategoryRepository.getCategorySummaries(userId);
    }
}

