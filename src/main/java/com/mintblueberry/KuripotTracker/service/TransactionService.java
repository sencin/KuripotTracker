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

import java.util.List;
import java.util.Optional;

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

    // READ ALL
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // READ BY ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    // UPDATE
    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequest request, String token) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        String userEmail = jwtService.extractEmail(token);
        if (!transaction.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to update this transaction");
        }

        var paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        var expenseCategory = expenseCategoryRepository.findById(request.getExpenseCategoryId())
                .orElseThrow(() -> new RuntimeException("Expense category not found"));

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setTime(request.getTime());
        transaction.setYear(request.getDate() != null ? String.valueOf(request.getDate().getYear()) : null);
        transaction.setPaymentType(paymentType);
        transaction.setExpenseCategory(expenseCategory);
        transaction.setDescription(request.getDescription());

        return transactionRepository.save(transaction);
    }

    // DELETE
    @Transactional
    public boolean deleteTransaction(Long id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) return false;

        transactionRepository.delete(transactionOpt.get());
        return true;
    }
}

