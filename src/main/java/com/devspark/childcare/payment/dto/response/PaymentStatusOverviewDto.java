package com.devspark.childcare.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusOverviewDto {
    private String status; // "Paid" or "Pending"
    private long count;
    private long amount;
}
