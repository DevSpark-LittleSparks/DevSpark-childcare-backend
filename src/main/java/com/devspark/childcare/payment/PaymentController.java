package com.devspark.childcare.payment;

import com.devspark.childcare.payment.dto.request.AdditionalChargeRequestDTO;
import com.devspark.childcare.payment.dto.request.GeneratePaymentsRequestDTO;
import com.devspark.childcare.payment.dto.request.PayAllConfirmRequestDTO;
import com.devspark.childcare.payment.dto.request.PayAllRequestDTO;
import com.devspark.childcare.payment.dto.request.PaymentConfirmRequestDTO;
import com.devspark.childcare.payment.dto.request.PaymentRequestDTO;
import com.devspark.childcare.payment.dto.response.ApiResponse;
import com.devspark.childcare.payment.dto.response.MonthlyRevenueDto;
import com.devspark.childcare.payment.dto.response.PayAllResponseDTO;
import com.devspark.childcare.payment.dto.response.PaymentResponseDTO;
import com.devspark.childcare.payment.dto.response.PaymentStatusOverviewDto;
import com.devspark.childcare.payment.dto.response.YearlyRevenueDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    /** Verify a 3D-Secure-completed PaymentIntent and finalize the payment */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequestDTO request) {
        PaymentResponseDTO result = paymentService.confirmPayment(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Payment confirmed"));
    }

    /** Parent: settle every outstanding invoice in one charge */
    @PostMapping("/pay-all")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<PayAllResponseDTO>> payAllOutstanding(
            Principal principal, @Valid @RequestBody PayAllRequestDTO request) {
        PayAllResponseDTO result = paymentService.payAllOutstanding(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(result, "Outstanding balance paid"));
    }

    /** Parent: finalize a 3D-Secure-completed full-balance payment */
    @PostMapping("/pay-all/confirm")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<PayAllResponseDTO>> confirmPayAll(
            Principal principal, @Valid @RequestBody PayAllConfirmRequestDTO request) {
        PayAllResponseDTO result = paymentService.confirmPayAll(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(result, "Payment confirmed"));
    }

    /** Parent: add a one-off charge (registration fee, facility fee, or other) to their own account */
    @PostMapping("/additional-charge")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createAdditionalCharge(
            Principal principal, @Valid @RequestBody AdditionalChargeRequestDTO request) {
        PaymentResponseDTO result = paymentService.createAdditionalCharge(principal.getName(), request);
        return ResponseEntity.status(201).body(ApiResponse.success(result, "Additional charge created"));
    }

    /** Admin: collected revenue grouped by billing month */
    @GetMapping("/revenue/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueDto>>> getMonthlyRevenue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMonthlyRevenue(), "Monthly revenue retrieved"));
    }

    /** Admin: collected revenue grouped by year */
    @GetMapping("/revenue/yearly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<YearlyRevenueDto>>> getYearlyRevenue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getYearlyRevenue(), "Yearly revenue retrieved"));
    }

    /** Admin: Paid vs Pending overview, to spot overdue payments */
    @GetMapping("/status-overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentStatusOverviewDto>>> getPaymentStatusOverview() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentStatusOverview(), "Payment status overview retrieved"));
    }
}
