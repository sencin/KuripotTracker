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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    private TransactionResponse mapToDto(Transaction t) {

        String amount = null;
        if (t.getAmount() != null) {
            amount = t.getAmount().toString();
        }

        String date = null;
        if (t.getDate() != null) {
            date = t.getDate().toString();
        }

        String time = null;
        if (t.getTime() != null) {
            time = t.getTime().toString();
        }

        String paymentTypeName = null;
        if (t.getPaymentType() != null) {
            paymentTypeName = t.getPaymentType().getName();
        }

        String expenseCategoryName = null;
        if (t.getExpenseCategory() != null) {
            expenseCategoryName = t.getExpenseCategory().getName();
        }

        return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType())
                .amount(amount)
                .date(date)
                .time(time)
                .year(t.getYear())
                .paymentTypeName(paymentTypeName)
                .expenseCategoryName(expenseCategoryName)
                .description(t.getDescription())
                .build();
    }


}
