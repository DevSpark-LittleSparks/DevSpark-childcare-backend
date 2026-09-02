package com.devspark.childcare.payment;

import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.payment.dto.request.AdditionalChargeRequestDTO;
import com.devspark.childcare.payment.dto.request.PaymentConfirmRequestDTO;
import com.devspark.childcare.payment.dto.request.PaymentRequestDTO;
import com.devspark.childcare.payment.dto.response.MonthlyRevenueDto;
import com.devspark.childcare.payment.dto.response.PaymentResponseDTO;
import com.devspark.childcare.payment.dto.response.PaymentStatusOverviewDto;
import com.devspark.childcare.payment.dto.response.YearlyRevenueDto;
import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
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
    private final CardDetailsRepository cardDetailsRepository;

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

    /**
     * Charges a saved Stripe payment method for the given payment. If Stripe
     * requires 3D Secure, this returns requiresAction=true + a clientSecret
     * for the frontend to confirm with stripe.confirmCardPayment(), followed
     * by a call to confirmPayment() below - the payment is never marked PAYED
     * based on a client-side claim alone.
     */
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

        CardDetails card = cardDetailsRepository.findById(request.getSavedCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Saved card not found: " + request.getSavedCardId()));

        if (!card.getParent().getParentId().equals(request.getParentId())) {
            throw new IllegalArgumentException("Card does not belong to this parent");
        }

        log.info("Charging payment {} for parent {} (billing: {})",
                payment.getPaymentId(), request.getParentId(), payment.getBillingMonth());

        PaymentIntent intent;
        try {
            // Stripe requires the owning Customer on the PaymentIntent whenever
            // the PaymentMethod is already attached to one - read it from
            // Stripe rather than storing our own copy that could go stale.
            com.stripe.model.PaymentMethod paymentMethod = com.stripe.model.PaymentMethod.retrieve(card.getStripePaymentMethodId());

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount() * 100)
                    .setCurrency("lkr")
                    .setPaymentMethod(card.getStripePaymentMethodId())
                    .addPaymentMethodType("card")
                    .setConfirm(true)
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC);

            if (paymentMethod.getCustomer() != null) {
                paramsBuilder.setCustomer(paymentMethod.getCustomer());
            }

            intent = PaymentIntent.create(paramsBuilder.build());
        } catch (StripeException e) {
            throw new IllegalArgumentException("Payment failed: " + e.getMessage());
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setRequestedAt(LocalDateTime.now());
        transaction.setGatewayReference(intent.getId());

        if ("requires_action".equals(intent.getStatus())) {
            transaction.setStatus(PaymentTransaction.TxnStatus.UNSUCCESSFUL);
            transaction = transactionRepository.save(transaction);
            payment.setTransaction(transaction);
            payment = paymentRepository.save(payment);

            PaymentResponseDTO dto = toResponseDTO(payment);
            dto.setRequiresAction(true);
            dto.setClientSecret(intent.getClientSecret());
            return dto;
        }

        boolean succeeded = "succeeded".equals(intent.getStatus());
        transaction.setTnxTime(LocalDateTime.now());
        transaction.setStatus(succeeded ? PaymentTransaction.TxnStatus.SUCCESSFUL : PaymentTransaction.TxnStatus.UNSUCCESSFUL);
        transaction = transactionRepository.save(transaction);

        payment.setTransaction(transaction);
        if (succeeded) {
            payment.setStatus(Payment.PaymentStatus.PAYED);
        }
        payment = paymentRepository.save(payment);

        if (!succeeded) {
            throw new IllegalArgumentException("Payment failed: card was declined");
        }

        log.info("Payment {} processed: {}", payment.getPaymentId(), transaction.getStatus());
        return toResponseDTO(payment);
    }

    /**
     * Verifies a 3D-Secure-completed PaymentIntent directly against Stripe
     * (never trusting the client's claim) and finalizes the payment.
     */
    @Transactional
    public PaymentResponseDTO confirmPayment(PaymentConfirmRequestDTO request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + request.getPaymentId()));

        if (!payment.getParent().getParentId().equals(request.getParentId())) {
            throw new IllegalArgumentException("Payment does not belong to this parent");
        }

        PaymentIntent intent;
        try {
            intent = PaymentIntent.retrieve(request.getPaymentIntentId());
        } catch (StripeException e) {
            throw new IllegalArgumentException("Unable to verify payment: " + e.getMessage());
        }

        boolean succeeded = "succeeded".equals(intent.getStatus());

        PaymentTransaction transaction = payment.getTransaction();
        if (transaction == null) {
            transaction = new PaymentTransaction();
            transaction.setRequestedAt(LocalDateTime.now());
        }
        transaction.setGatewayReference(intent.getId());
        transaction.setTnxTime(LocalDateTime.now());
        transaction.setStatus(succeeded ? PaymentTransaction.TxnStatus.SUCCESSFUL : PaymentTransaction.TxnStatus.UNSUCCESSFUL);
        transaction = transactionRepository.save(transaction);

        payment.setTransaction(transaction);
        if (succeeded) {
            payment.setStatus(Payment.PaymentStatus.PAYED);
        }
        payment = paymentRepository.save(payment);

        if (!succeeded) {
            throw new IllegalArgumentException("Payment was not completed (status: " + intent.getStatus() + ")");
        }

        log.info("Payment {} confirmed via 3DS: {}", payment.getPaymentId(), transaction.getStatus());
        return toResponseDTO(payment);
    }

    /**
     * Parent-initiated one-off charge (registration fee, facility fee, or a
     * free-text "other" charge) - created as NOT_PAYED, paid immediately
     * afterward through the normal processPayment() flow.
     */
    @Transactional
    public PaymentResponseDTO createAdditionalCharge(String requesterEmail, AdditionalChargeRequestDTO request) {
        Parent parent = ensureOwnership(requesterEmail, request.getParentId());

        String description = switch (request.getChargeType()) {
            case "REGISTRATION_FEE" -> "Registration Fee";
            case "FACILITY_FEE" -> "Facility Fee";
            case "OTHER" -> {
                if (request.getDescription() == null || request.getDescription().isBlank()) {
                    throw new IllegalArgumentException("A description is required for 'Other' charges");
                }
                yield request.getDescription();
            }
            default -> throw new IllegalArgumentException("Unknown charge type: " + request.getChargeType());
        };

        Payment payment = new Payment();
        payment.setBillingMonth(YearMonth.now().toString());
        payment.setDescription(description);
        payment.setAmount(request.getAmount());
        payment.setStatus(Payment.PaymentStatus.NOT_PAYED);
        payment.setParent(parent);
        payment = paymentRepository.save(payment);

        log.info("Additional charge created: {} ({}) for parent {}", description, request.getAmount(), parent.getParentId());
        return toResponseDTO(payment);
    }

    /** Admin: create NOT_PAYED records for all active parents for a billing month. */
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

    /** Admin: total collected (PAYED) revenue grouped by billing month ("YYYY-MM"). */
    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        return paymentRepository.findByStatus(Payment.PaymentStatus.PAYED).stream()
                .collect(Collectors.groupingBy(Payment::getBillingMonth, Collectors.summingLong(Payment::getAmount)))
                .entrySet().stream()
                .map(e -> MonthlyRevenueDto.builder().month(e.getKey()).revenue(e.getValue()).build())
                .sorted(Comparator.comparing(MonthlyRevenueDto::getMonth))
                .collect(Collectors.toList());
    }

    /** Admin: total collected (PAYED) revenue grouped by year. */
    public List<YearlyRevenueDto> getYearlyRevenue() {
        return paymentRepository.findByStatus(Payment.PaymentStatus.PAYED).stream()
                .collect(Collectors.groupingBy(
                        p -> Integer.parseInt(p.getBillingMonth().split("-")[0]),
                        Collectors.summingLong(Payment::getAmount)))
                .entrySet().stream()
                .map(e -> YearlyRevenueDto.builder().year(e.getKey()).revenue(e.getValue()).build())
                .sorted(Comparator.comparing(YearlyRevenueDto::getYear))
                .collect(Collectors.toList());
    }

    /** Admin: Paid vs Pending payment counts/amounts, to spot overdue fees. */
    public List<PaymentStatusOverviewDto> getPaymentStatusOverview() {
        List<Payment> all = paymentRepository.findAll();

        long paidCount = all.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.PAYED).count();
        long paidAmount = all.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.PAYED)
                .mapToLong(Payment::getAmount).sum();
        long pendingCount = all.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.NOT_PAYED).count();
        long pendingAmount = all.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.NOT_PAYED)
                .mapToLong(Payment::getAmount).sum();

        return List.of(
                PaymentStatusOverviewDto.builder().status("Paid").count(paidCount).amount(paidAmount).build(),
                PaymentStatusOverviewDto.builder().status("Pending").count(pendingCount).amount(pendingAmount).build()
        );
    }

    private Parent ensureOwnership(String requesterEmail, UUID parentId) {
        Parent parent = parentRepository.findByAccountEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for " + requesterEmail));
        if (!parent.getParentId().equals(parentId)) {
            throw new IllegalArgumentException("You can only act on your own account");
        }
        return parent;
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
        dto.setDescription(payment.getDescription());
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
