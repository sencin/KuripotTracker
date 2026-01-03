package com.mintblueberry.KuripotTracker.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TransactionRequest {
    private String type; // INCOME or EXPENSE
    private BigDecimal amount;
    private LocalDate date;
    private LocalTime time;
    private Long paymentTypeId;
    private Long expenseCategoryId;
    private String description;
}
