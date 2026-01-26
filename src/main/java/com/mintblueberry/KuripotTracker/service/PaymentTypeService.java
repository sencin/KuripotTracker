package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.entity.PaymentType;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.PaymentTypeRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Create
    @Transactional
    public PaymentType createPaymentType(String token, String name, String image) {
        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentType paymentType = PaymentType.builder()
                .name(name)
                .image(image) // can be null
                .user(user)
                .build();

        return paymentTypeRepository.save(paymentType);
    }

    // Read all for a user
    public List<PaymentType> getAllPaymentTypes(String token) {
        Long userId = jwtService.extractUserId(token);
        return paymentTypeRepository.findAllByUserId(userId);
    }

    public Optional<PaymentType> getPaymentTypeById(String token, Long id) {
        Long userId = jwtService.extractUserId(token);
        return paymentTypeRepository.findByIdAndUserId(id, userId);
    }
    // Update
    @Transactional
    public PaymentType updatePaymentType(String token, Long id, String name, String image) {
        Long userId = jwtService.extractUserId(token);
        PaymentType paymentType = paymentTypeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        if (name != null && !name.isBlank()) paymentType.setName(name);
        if (image != null) paymentType.setImage(image);

        return paymentTypeRepository.save(paymentType);
    }

    // Delete
    @Transactional
    public void deletePaymentType(String token, Long id) {
        Long userId = jwtService.extractUserId(token);
        PaymentType paymentType = paymentTypeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        paymentTypeRepository.delete(paymentType);
    }
}
