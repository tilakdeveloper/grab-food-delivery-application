package com.grab.FoodApp.exceptions;

public class PaymentProcessingException extends RuntimeException{

    public PaymentProcessingException(String message) {
        super(message);
    }
}
