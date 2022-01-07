package com.rodrigofigueiredo.authorizationTransaction.exceptions;


public class TransactionAuthorizationException extends RuntimeException {
    public TransactionAuthorizationException() {
        super("Transaction Authorization Exception");
    }

    public TransactionAuthorizationException(String message) {
        super(message);
    }
}
