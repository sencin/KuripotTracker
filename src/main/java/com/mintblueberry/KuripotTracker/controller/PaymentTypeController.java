package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.PaymentTypeRequest;
import com.mintblueberry.KuripotTracker.entity.PaymentType;
import com.mintblueberry.KuripotTracker.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/payment-types")
@RequiredArgsConstructor
public class PaymentTypeController {

    private final PaymentTypeRepository paymentTypeRepository;

    // Create
    @PostMapping
    public ResponseEntity<LinkedHashMap<String, Object>> createPaymentType(@RequestBody PaymentTypeRequest request) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            PaymentType paymentType = new PaymentType();
            paymentType.setName(request.getName());
            paymentTypeRepository.save(paymentType);

            response.put("success", true);
            response.put("paymentType", paymentType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Read all
    @GetMapping
    public List<PaymentType> getAllPaymentTypes() {
        return paymentTypeRepository.findAll();
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentType> getPaymentTypeById(@PathVariable Long id) {
        return paymentTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<LinkedHashMap<String, Object>> updatePaymentType(
            @PathVariable Long id,
            @RequestBody PaymentTypeRequest request
    ) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        return paymentTypeRepository.findById(id).map(paymentType -> {
            paymentType.setName(request.getName());
            paymentTypeRepository.save(paymentType);
            response.put("success", true);
            response.put("paymentType", paymentType);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "PaymentType not found");
            return ResponseEntity.badRequest().body(response);
        });
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<LinkedHashMap<String, Object>> deletePaymentType(@PathVariable Long id) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        return paymentTypeRepository.findById(id).map(paymentType -> {
            paymentTypeRepository.delete(paymentType);
            response.put("success", true);
            response.put("message", "Deleted successfully");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "PaymentType not found");
            return ResponseEntity.badRequest().body(response);
        });
    }
}

