package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.TransactionRequest;
import com.mintblueberry.KuripotTracker.dto.TransactionResponse;
import com.mintblueberry.KuripotTracker.entity.Transaction;
import com.mintblueberry.KuripotTracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

            TransactionResponse transactionResponse = transactionService.createTransaction(request, token);

            response.put("message", "Transaction Recorded");
            response.put("transaction", transactionResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

//    // READ ALL
//    @GetMapping
//    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
//        List<TransactionResponse> transactions = transactionService.getAllTransactions();
//        return ResponseEntity.ok(transactions);
//    }


//    // READ BY ID
//    @GetMapping("/{id}")
//    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
//        try {
//            TransactionResponse transaction = transactionService.getTransactionById(id);
//            return ResponseEntity.ok(transaction);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//
//    // UPDATE
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateTransaction(
//            @PathVariable Long id,
//            @RequestBody TransactionRequest request,
//            @RequestHeader("Authorization") String authorizationHeader
//    ) {
//        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
//        try {
//            String token = authorizationHeader.substring(7);
//            TransactionResponse updated = transactionService.updateTransaction(id, request, token);
//            response.put("message", "Transaction Updated");
//            response.put("transaction", updated);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    // DELETE
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteTransaction(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.substring(7);

    transactionService.deleteTransaction(id, token);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("message", "Transaction deleted");
    return ResponseEntity.ok(response);
}


    @GetMapping("/me")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String type // optional: "INCOME" or "EXPENSE"
    ) {
        List<TransactionResponse> transactions = transactionService.getMyTransactions(authorizationHeader, type);
        return ResponseEntity.ok(transactions);
    }



}
