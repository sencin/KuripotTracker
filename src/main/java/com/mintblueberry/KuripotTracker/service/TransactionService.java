package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.TransactionRequest;
import com.mintblueberry.KuripotTracker.entity.ExpenseCategory;
import com.mintblueberry.KuripotTracker.entity.PaymentType;
import com.mintblueberry.KuripotTracker.entity.Transaction;
import com.mintblueberry.KuripotTracker.entity.User;
import com.mintblueberry.KuripotTracker.repository.ExpenseCategoryRepository;
import com.mintblueberry.KuripotTracker.repository.PaymentTypeRepository;
import com.mintblueberry.KuripotTracker.repository.TransactionRepository;
import com.mintblueberry.KuripotTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final JwtService jwtService;

    @Transactional
    public Transaction createTransaction(TransactionRequest request, String bearerToken) {
        // Extract email from Bearer token
        String email = jwtService.extractEmail(bearerToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("PaymentType not found"));

        ExpenseCategory expenseCategory = expenseCategoryRepository.findById(request.getExpenseCategoryId())
                .orElseThrow(() -> new RuntimeException("ExpenseCategory not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(request.getType())
                .amount(request.getAmount())
                .date(request.getDate())
                .time(request.getTime())
                .year(String.valueOf(request.getDate().getYear()))
                .paymentType(paymentType)
                .expenseCategory(expenseCategory)
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }
}

