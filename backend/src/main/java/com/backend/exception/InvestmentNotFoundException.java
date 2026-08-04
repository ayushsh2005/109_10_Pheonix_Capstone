package com.backend.exception;

public class InvestmentNotFoundException extends RuntimeException {

    public InvestmentNotFoundException(String message) {
        super(message);
    }

    public InvestmentNotFoundException(Long id) {
        super("Investment not found with id: " + id);
    }
}
