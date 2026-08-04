package com.backend.exception;

public class PortfolioNotFoundException extends RuntimeException {

    public PortfolioNotFoundException(String message) {
        super(message);
    }

    public PortfolioNotFoundException(Long id) {
        super("Portfolio not found with id: " + id);
    }
}
