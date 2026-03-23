package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.ExpenseCategoryRequest;
import com.mintblueberry.KuripotTracker.dto.ExpenseCategoryResponse;
import com.mintblueberry.KuripotTracker.dto.ExpenseCategorySummary;
import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import com.mintblueberry.KuripotTracker.repository.ExpenseCategoryRepository;
import com.mintblueberry.KuripotTracker.service.ExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    // Create
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request
    ) {
        String name = request.get("name");
        String image = request.get("image"); // may be null

        ExpenseCategory category = expenseCategoryService.createExpenseCategory(
                token.substring(7),
                name,
                image
        );

        Map<String, Object> response = Map.of(
                "id", category.getId(),
                "name", category.getName()
        );

        return ResponseEntity.ok(response);
    }


    // Read all
    @GetMapping
    public ResponseEntity<List<ExpenseCategoryResponse>> getCategories(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        List<ExpenseCategory> categories = expenseCategoryService.getAllExpenseCategories(token);

        // Map entities to DTOs
        List<ExpenseCategoryResponse> result = categories.stream().map(c -> {
            ExpenseCategoryResponse dto = new ExpenseCategoryResponse();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setImage(c.getImage());
            return dto;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/expense-summary")
    public ResponseEntity<List<ExpenseCategorySummary>> getSummary(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                expenseCategoryService.getCategorySummaries(token.substring(7))
        );
    }

    // Read by ID
    @GetMapping("/{id}")
    public ExpenseCategory getCategory(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return expenseCategoryService.getExpenseCategoryById(token.substring(7), id);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String name = request.get("name");     // optional
        String image = request.get("image");   // optional

        ExpenseCategory category = expenseCategoryService.updateExpenseCategory(
                token.substring(7),
                id,
                name,
                image
        );

        return ResponseEntity.ok(Map.of("success", true, "expenseCategory", category));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id) {
        expenseCategoryService.deleteExpenseCategory(token.substring(7), id);
        return ResponseEntity.ok(Map.of( "message", "Deleted successfully"));
    }
}

