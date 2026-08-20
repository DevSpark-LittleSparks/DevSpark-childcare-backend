package com.example.Little.sparks.payment.payment.dto.request;

import com.example.Little.sparks.payment.payment.CardDetails;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    // Use a saved card by ID, or provide card details below
    private UUID savedCardId;

    // New card details (required when savedCardId is null)
    private String cardHolderName;

    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be 13-19 digits")
    private String cardNumber;

    private CardDetails.CardType cardType;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry must be MM/YY format")
    private String expiryDate;

    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;

    private boolean saveCard = false;
}
