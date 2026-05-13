package com.example.Little.sparks.payment.controller;

import com.example.Little.sparks.payment.dto.request.GeneratePaymentsRequestDTO;
import com.example.Little.sparks.payment.dto.request.PaymentRequestDTO;
import com.example.Little.sparks.payment.dto.response.ApiResponse;
import com.example.Little.sparks.payment.dto.response.PaymentResponseDTO;
import com.example.Little.sparks.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** Get all payments (paid + unpaid) for a parent */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByParent(
            @PathVariable UUID parentId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByParent(parentId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved"));
    }

    /** Get only unpaid payments for a parent */
    @GetMapping("/parent/{parentId}/pending")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPendingPayments(
            @PathVariable UUID parentId) {
        List<PaymentResponseDTO> payments = paymentService.getPendingPayments(parentId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Pending payments retrieved"));
    }

    /** Get payment history (paid payments) for a parent */
    @GetMapping("/parent/{parentId}/history")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentHistory(
            @PathVariable UUID parentId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentHistory(parentId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payment history retrieved"));
    }

    /** Get a single payment by ID */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(
            @PathVariable UUID paymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment retrieved"));
    }

    /** Process (pay) a payment */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO result = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Payment processed successfully"));
    }

    /** Admin: generate monthly payment records for all parents */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateMonthlyPayments(
            @Valid @RequestBody GeneratePaymentsRequestDTO request) {
        int count = paymentService.generateMonthlyPayments(request.getBillingMonth(), request.getAmount());
        return ResponseEntity.ok(ApiResponse.success(
                count + " payment records created for " + request.getBillingMonth(),
                "Monthly payments generated"));
    }
}
