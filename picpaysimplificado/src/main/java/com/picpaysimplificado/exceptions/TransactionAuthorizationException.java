package com.picpaysimplificado.exceptions;

public class TransactionAuthorizationException extends RuntimeException {
    public TransactionAuthorizationException(String message) {
        super(message);
    }
}