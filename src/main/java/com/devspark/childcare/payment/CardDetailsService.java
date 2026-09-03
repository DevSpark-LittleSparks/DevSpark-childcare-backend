package com.devspark.childcare.payment;

import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.payment.dto.request.CardDetailsRequestDTO;
import com.devspark.childcare.payment.dto.response.CardDetailsResponseDTO;
import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardDetailsService {

    private final CardDetailsRepository cardDetailsRepository;
    private final ParentRepository parentRepository;

    @Transactional
    public CardDetailsResponseDTO saveCard(CardDetailsRequestDTO request) {
        Parent parent = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + request.getParentId()));

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.retrieve(request.getStripePaymentMethodId());
        } catch (StripeException e) {
            throw new IllegalArgumentException("Invalid Stripe payment method: " + e.getMessage());
        }

        PaymentMethod.Card card = paymentMethod.getCard();
        if (card == null) {
            throw new IllegalArgumentException("Payment method is not a card");
        }

        // Stripe only lets an unattached PaymentMethod back a single
        // PaymentIntent, so a card saved without a Customer works once and
        // then fails forever. Attach it to this parent's Customer instead.
        try {
            String customerId = parent.getStripeCustomerId();
            if (customerId == null || customerId.isBlank()) {
                customerId = Customer.create(CustomerCreateParams.builder()
                        .setName(parent.getFullName())
                        .putMetadata("parent_id", parent.getParentId().toString())
                        .build()).getId();
                parent.setStripeCustomerId(customerId);
                parentRepository.save(parent);
            }

            if (paymentMethod.getCustomer() == null) {
                paymentMethod = paymentMethod.attach(PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build());
            } else if (!paymentMethod.getCustomer().equals(customerId)) {
                throw new IllegalArgumentException(
                        "This payment method is already attached to a different customer");
            }
        } catch (StripeException e) {
            throw new IllegalArgumentException("Could not attach card to customer: " + e.getMessage());
        }

        CardDetails details = new CardDetails();
        details.setCardHolderName(request.getCardHolderName());
        details.setCardLast4(card.getLast4());
        details.setCardType(mapBrand(card.getBrand()));
        details.setExpDate(LocalDateTime.of(card.getExpYear().intValue(), card.getExpMonth().intValue(), 1, 0, 0, 0));
        details.setStripePaymentMethodId(paymentMethod.getId());
        details.setPaidVia("STRIPE");
        details.setParent(parent);

        details = cardDetailsRepository.save(details);
        return toResponseDTO(details);
    }

    public List<CardDetailsResponseDTO> getCardsByParent(UUID parentId) {
        if (!parentRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent not found: " + parentId);
        }
        return cardDetailsRepository.findByParent_ParentId(parentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCard(UUID cardId) {
        if (!cardDetailsRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Card not found: " + cardId);
        }
        cardDetailsRepository.deleteById(cardId);
    }

    private CardDetails.CardType mapBrand(String stripeBrand) {
        if (stripeBrand == null) return CardDetails.CardType.UNKNOWN;
        return switch (stripeBrand.toLowerCase()) {
            case "visa" -> CardDetails.CardType.VISA;
            case "mastercard" -> CardDetails.CardType.MASTERCARD;
            case "amex" -> CardDetails.CardType.AMEX;
            case "discover" -> CardDetails.CardType.DISCOVER;
            default -> CardDetails.CardType.UNKNOWN;
        };
    }

    private CardDetailsResponseDTO toResponseDTO(CardDetails card) {
        return CardDetailsResponseDTO.builder()
                .cardDetailsId(card.getCardDetailsId())
                .cardHolderName(card.getCardHolderName())
                .cardLast4(card.getCardLast4())
                .cardType(card.getCardType())
                .expDate(card.getExpDate())
                .parentId(card.getParent().getParentId())
                .build();
    }
}
