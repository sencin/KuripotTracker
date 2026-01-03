package com.mintblueberry.KuripotTracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String type;
    private String amount;
    private String date;
    private String time;
    private String year;
    private String paymentTypeName;
    private String expenseCategoryName;
    private String description;


}
