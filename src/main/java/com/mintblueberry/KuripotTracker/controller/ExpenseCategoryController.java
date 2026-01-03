package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.ExpenseCategoryRequest;
import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import com.mintblueberry.KuripotTracker.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    // Create
    @PostMapping
    public ResponseEntity<LinkedHashMap<String, Object>> createExpenseCategory(@RequestBody ExpenseCategoryRequest request) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            ExpenseCategory category = new ExpenseCategory();
            category.setName(request.getName());
            expenseCategoryRepository.save(category);

            response.put("success", true);
            response.put("expenseCategory", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Read all
    @GetMapping
    public List<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategory> getExpenseCategoryById(@PathVariable Long id) {
        return expenseCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<LinkedHashMap<String, Object>> updateExpenseCategory(
            @PathVariable Long id,
            @RequestBody ExpenseCategoryRequest request
    ) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        return expenseCategoryRepository.findById(id).map(category -> {
            category.setName(request.getName());
            expenseCategoryRepository.save(category);
            response.put("success", true);
            response.put("expenseCategory", category);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "ExpenseCategory not found");
            return ResponseEntity.badRequest().body(response);
        });
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<LinkedHashMap<String, Object>> deleteExpenseCategory(@PathVariable Long id) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        return expenseCategoryRepository.findById(id).map(category -> {
            expenseCategoryRepository.delete(category);
            response.put("success", true);
            response.put("message", "Deleted successfully");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "ExpenseCategory not found");
            return ResponseEntity.badRequest().body(response);
        });
    }
}

