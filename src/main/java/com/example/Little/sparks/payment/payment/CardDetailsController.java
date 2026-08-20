package com.example.Little.sparks.payment.payment;

import com.example.Little.sparks.payment.payment.dto.request.CardDetailsRequestDTO;
import com.example.Little.sparks.payment.payment.dto.response.ApiResponse;
import com.example.Little.sparks.payment.payment.dto.response.CardDetailsResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardDetailsController {

    private final CardDetailsService cardDetailsService;

    /** Save a new card for a parent */
    @PostMapping
    public ResponseEntity<ApiResponse<CardDetailsResponseDTO>> saveCard(
            @Valid @RequestBody CardDetailsRequestDTO request) {
        CardDetailsResponseDTO card = cardDetailsService.saveCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(card, "Card saved successfully"));
    }

    /** Get all saved cards for a parent */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<CardDetailsResponseDTO>>> getCardsByParent(
            @PathVariable UUID parentId) {
        List<CardDetailsResponseDTO> cards = cardDetailsService.getCardsByParent(parentId);
        return ResponseEntity.ok(ApiResponse.success(cards, "Cards retrieved"));
    }

    /** Delete a saved card */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(@PathVariable UUID cardId) {
        cardDetailsService.deleteCard(cardId);
        return ResponseEntity.ok(ApiResponse.success(null, "Card deleted successfully"));
    }
}
