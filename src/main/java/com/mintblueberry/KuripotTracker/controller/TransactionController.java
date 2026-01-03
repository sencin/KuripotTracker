package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.TransactionRequest;
import com.mintblueberry.KuripotTracker.dto.TransactionResponse;
import com.mintblueberry.KuripotTracker.entity.Transaction;
import com.mintblueberry.KuripotTracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // CREATE
    @PostMapping
    public ResponseEntity<LinkedHashMap<String, Object>> createTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            var transaction = transactionService.createTransaction(request, token);

            TransactionResponse transactionResponse = mapToDto(transaction);

            response.put("message", "Transaction Recorded");
            response.put("transaction", transactionResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        var transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToDto(transaction));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            var updated = transactionService.updateTransaction(id, request, token);

            TransactionResponse transactionResponse = mapToDto(updated);
            response.put("message", "Transaction Updated");
            response.put("transaction", transactionResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        boolean deleted = transactionService.deleteTransaction(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Helper to map Transaction -> TransactionResponse
    private TransactionResponse mapToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount() != null ? transaction.getAmount().toString() : null)
                .date(transaction.getDate() != null ? transaction.getDate().toString() : null)
                .time(transaction.getTime() != null ? transaction.getTime().toString() : null)
                .year(transaction.getYear())
                .paymentTypeName(transaction.getPaymentType().getName())
                .expenseCategoryName(transaction.getExpenseCategory().getName())
                .description(transaction.getDescription())
                .build();
    }
}
