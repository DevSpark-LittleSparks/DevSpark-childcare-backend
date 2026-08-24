package com.devspark.childcare.payment;

public class PaymentAlreadyPaidException extends RuntimeException {

    public PaymentAlreadyPaidException(String message) {
        super(message);
    }
}
