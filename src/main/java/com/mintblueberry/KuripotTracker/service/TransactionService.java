package com.mintblueberry.KuripotTracker.service;

import com.mintblueberry.KuripotTracker.dto.TransactionRequest;
import com.mintblueberry.KuripotTracker.dto.TransactionResponse;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final JwtService jwtService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String bearerToken) {
        // Extract email from Bearer token
        String email = jwtService.extractEmail(bearerToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("PaymentType not found"));

        ExpenseCategory expenseCategory = null;

        // EXPENSE transactions must have a category
        if ("EXPENSE".equalsIgnoreCase(request.getType())) {
            if (request.getExpenseCategoryId() == null) {
                throw new IllegalStateException("Expense transaction must have an expense category");
            }
            expenseCategory = expenseCategoryRepository.findById(request.getExpenseCategoryId())
                    .orElseThrow(() -> new RuntimeException("ExpenseCategory not found"));
        }

        // INCOME transactions cannot have a category
        if ("INCOME".equalsIgnoreCase(request.getType()) && request.getExpenseCategoryId() != null) {
            throw new IllegalStateException("Income transaction cannot have an expense category");
        }

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

        transaction = transactionRepository.save(transaction);

        // Return the DTO, not the entity
        return mapToDto(transaction);
    }


    // READ ALL
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        // Map entities to DTOs
        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    // READ BY ID
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return mapToDto(transaction);
    }

    public List<TransactionResponse> getMyTransactions(String authorizationHeader, String type) {
        // Extract email from token
        String token = authorizationHeader.substring(7);
        String email = jwtService.extractEmail(token);

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions;

        // Apply optional type filter
        if (type == null) {
            transactions = transactionRepository.findByUserId(user.getId());
        } else {
            transactions = transactionRepository.findByUserIdAndType(user.getId(), type.toUpperCase());
        }

        // Map to DTOs
        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // UPDATE
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, String token) {
        // Fetch the transaction
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check ownership
        String userEmail = jwtService.extractEmail(token);
        if (!transaction.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to update this transaction");
        }

        // Fetch payment type
        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        // Handle expense category
        ExpenseCategory expenseCategory = null;

        if ("EXPENSE".equalsIgnoreCase(request.getType())) {
            if (request.getExpenseCategoryId() == null) {
                throw new IllegalStateException("Expense transaction must have an expense category");
            }
            expenseCategory = expenseCategoryRepository.findById(request.getExpenseCategoryId())
                    .orElseThrow(() -> new RuntimeException("Expense category not found"));
        }

        if ("INCOME".equalsIgnoreCase(request.getType()) && request.getExpenseCategoryId() != null) {
            throw new IllegalStateException("Income transaction cannot have an expense category");
        }

        // Update fields
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setTime(request.getTime());
        transaction.setYear(request.getDate() != null ? String.valueOf(request.getDate().getYear()) : null);
        transaction.setPaymentType(paymentType);
        transaction.setExpenseCategory(expenseCategory);
        transaction.setDescription(request.getDescription());

        // Save and return DTO
        transaction = transactionRepository.save(transaction);
        return mapToDto(transaction);
    }


    // DELETE (owner-only)
    @Transactional
    public void deleteTransaction(Long transactionId, String token) {

        Long userId = jwtService.extractUserId(token);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        System.out.println("JWT userId: " + userId);
        System.out.println("Transaction ownerId: " + transaction.getUser().getId());

        // OWNER CHECK — ID vs ID
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }



    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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

