package com.example.Little.sparks.payment.payment;

public class PaymentAlreadyPaidException extends RuntimeException {

    public PaymentAlreadyPaidException(String message) {
        super(message);
    }
}
