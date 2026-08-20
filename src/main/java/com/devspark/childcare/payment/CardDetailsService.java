package com.devspark.childcare.payment;

import com.devspark.childcare.payment.dto.request.CardDetailsRequestDTO;
import com.devspark.childcare.payment.dto.response.CardDetailsResponseDTO;
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

        CardDetails card = new CardDetails();
        card.setCardHolderName(request.getCardHolderName());
        card.setCardLast4(request.getCardNumber().substring(request.getCardNumber().length() - 4));
        card.setCardType(request.getCardType());
        card.setExpDate(parseExpiry(request.getExpiryDate()));
        card.setParent(parent);

        card = cardDetailsRepository.save(card);
        return toResponseDTO(card);
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

    // Parses "MM/YY" -> first day of that month
    private LocalDateTime parseExpiry(String expiryDate) {
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        return LocalDateTime.of(year, month, 1, 0, 0, 0);
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
