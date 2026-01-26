package com.mintblueberry.KuripotTracker.controller;

import com.mintblueberry.KuripotTracker.dto.PaymentTypeRequest;
import com.mintblueberry.KuripotTracker.entity.PaymentType;
import com.mintblueberry.KuripotTracker.repository.PaymentTypeRepository;
import com.mintblueberry.KuripotTracker.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-types")
@RequiredArgsConstructor
public class PaymentTypeController {


    private final PaymentTypeService paymentTypeService;

    // Create
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token,
                                    @RequestBody Map<String, String> request) {
        PaymentType type = paymentTypeService.createPaymentType(
                token.substring(7),
                request.get("name"),
                request.get("image")
        );
        Map<String, Object> response = new HashMap<>();
        response.put("id", type.getId());
        response.put("name", type.getName());
        response.put("image", type.getImage());

        return ResponseEntity.ok(response);
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestHeader("Authorization") String token) {
        List<PaymentType> types = paymentTypeService.getAllPaymentTypes(token.substring(7));

        List<Map<String, Object>> result = types.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("name", t.getName());
            map.put("image", t.getImage());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }


    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPaymentTypeById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id
    ) {
        String token = authorizationHeader.substring(7);

        return paymentTypeService.getPaymentTypeById(token, id)
                .map(pt -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", pt.getId());
                    result.put("name", pt.getName());
                    result.put("image", pt.getImage());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id,
                                    @RequestBody Map<String, String> request) {
        PaymentType type = paymentTypeService.updatePaymentType(
                token.substring(7),
                id,
                request.get("name"),
                request.get("image")
        );
        return ResponseEntity.ok(Map.of(
                "id", type.getId(),
                "name", type.getName(),
                "image", type.getImage()
        ));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        paymentTypeService.deletePaymentType(token.substring(7), id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}

