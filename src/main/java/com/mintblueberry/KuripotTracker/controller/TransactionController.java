package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.TransactionRequest;
import com.mintblueberry.KuripotTracker.dto.TransactionResponse;
import com.mintblueberry.KuripotTracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<LinkedHashMap<String, Object>> createTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        try {
            // Remove "Bearer " prefix
            String token = authorizationHeader.substring(7);

            var transaction = transactionService.createTransaction(request, token);

            TransactionResponse transactionResponse = TransactionResponse.builder()
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

            response.put("message", "Transaction Recorded");
            response.put("transaction", transactionResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
