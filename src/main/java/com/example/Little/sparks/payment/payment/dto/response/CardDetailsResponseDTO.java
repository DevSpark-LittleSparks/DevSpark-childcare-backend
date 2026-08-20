package com.example.Little.sparks.payment.payment.dto.response;

import com.example.Little.sparks.payment.payment.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailsResponseDTO {

    private UUID cardDetailsId;
    private String cardHolderName;
    private String cardLast4;
    private CardDetails.CardType cardType;
    private LocalDateTime expDate;
    private UUID parentId;
}
