package com.devspark.childcare.payment;

import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.payment.dto.request.PaymentRequestDTO;
import com.devspark.childcare.payment.dto.response.PaymentResponseDTO;
import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final ParentRepository parentRepository;

    public List<PaymentResponseDTO> getPaymentsByParent(UUID parentId) {
        ensureParentExists(parentId);
        return paymentRepository.findByParent_ParentId(parentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentResponseDTO> getPendingPayments(UUID parentId) {
        ensureParentExists(parentId);
        return paymentRepository.findByParent_ParentIdAndStatus(parentId, Payment.PaymentStatus.NOT_PAYED)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentResponseDTO> getPaymentHistory(UUID parentId) {
        ensureParentExists(parentId);
        return paymentRepository.findByParent_ParentIdAndStatus(parentId, Payment.PaymentStatus.PAYED)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return toResponseDTO(payment);
    }

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + request.getPaymentId()));

        if (!payment.getParent().getParentId().equals(request.getParentId())) {
            throw new IllegalArgumentException("Payment does not belong to this parent");
        }

        if (payment.getStatus() == Payment.PaymentStatus.PAYED) {
            throw new PaymentAlreadyPaidException(
                    "Payment for " + payment.getBillingMonth() + " is already paid");
        }

        // Validate card info is provided
        boolean usingSavedCard = request.getSavedCardId() != null;
        boolean usingNewCard = request.getCardNumber() != null && !request.getCardNumber().isBlank();
        if (!usingSavedCard && !usingNewCard) {
            throw new IllegalArgumentException("Card details or a saved card ID must be provided");
        }

        log.info("Processing payment {} for parent {} (billing: {})",
                payment.getPaymentId(), request.getParentId(), payment.getBillingMonth());

        boolean gatewaySuccess = callPaymentGateway(request);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTnxTime(LocalDateTime.now());
        transaction.setGatewayReference(UUID.randomUUID().toString());
        transaction.setStatus(gatewaySuccess
                ? PaymentTransaction.TxnStatus.SUCCESSFUL
                : PaymentTransaction.TxnStatus.UNSUCCESSFUL);
        transaction = transactionRepository.save(transaction);

        if (gatewaySuccess) {
            payment.setStatus(Payment.PaymentStatus.PAYED);
        }
        payment.setTransaction(transaction);
        payment = paymentRepository.save(payment);

        log.info("Payment {} processed: {}", payment.getPaymentId(), transaction.getStatus());
        return toResponseDTO(payment);
    }

    /**
     * Admin: create NOT_PAYED records for all active parents for a billing month.
     * Skips parents who already have a record for that month.
     */
    @Transactional
    public int generateMonthlyPayments(String billingMonth, Long amount) {
        List<Parent> parents = parentRepository.findAll();
        int created = 0;
        for (Parent parent : parents) {
            if (!paymentRepository.existsByParent_ParentIdAndBillingMonth(parent.getParentId(), billingMonth)) {
                Payment payment = new Payment();
                payment.setBillingMonth(billingMonth);
                payment.setAmount(amount);
                payment.setStatus(Payment.PaymentStatus.NOT_PAYED);
                payment.setParent(parent);
                paymentRepository.save(payment);
                created++;
            }
        }
        log.info("Generated {} payment records for billing month {}", created, billingMonth);
        return created;
    }

    // Simulate payment gateway (replace with Stripe/PayHere integration)
    private boolean callPaymentGateway(PaymentRequestDTO request) {
        return true;
    }

    private void ensureParentExists(UUID parentId) {
        if (!parentRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent not found: " + parentId);
        }
    }

    private PaymentResponseDTO toResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setBillingMonth(payment.getBillingMonth());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setParentId(payment.getParent().getParentId());
        dto.setParentName(payment.getParent().getFullName());

        if (payment.getTransaction() != null) {
            dto.setTxnId(payment.getTransaction().getTxnId());
            dto.setTxnTime(payment.getTransaction().getTnxTime());
            dto.setTxnStatus(payment.getTransaction().getStatus());
            dto.setGatewayReference(payment.getTransaction().getGatewayReference());
        }
        return dto;
    }
}
