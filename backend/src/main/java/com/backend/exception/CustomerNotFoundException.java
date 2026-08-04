package com.backend.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}
