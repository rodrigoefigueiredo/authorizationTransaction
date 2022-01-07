package com.rodrigofigueiredo.authorizationTransaction.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void isFirstTransaction() {
        Account statusTransaction = new Account(true, 100, new ArrayList<Transaction>());
        assertTrue(statusTransaction.isFirstTransaction());
        statusTransaction.setAvailableLimit(0);
        statusTransaction.setActive(false);
        assertTrue(statusTransaction.isFirstTransaction());
        statusTransaction.setHistory(null);
        assertTrue(statusTransaction.isFirstTransaction());
        Transaction transaction = new Transaction(100, "Loja do João", LocalDateTime.now());
        List<Transaction> history = new ArrayList<Transaction>();
        history.add(transaction);
        statusTransaction.setHistory(history);
        assertFalse(statusTransaction.isFirstTransaction());
        statusTransaction.getHistory().add(new Transaction(20, "Loja do João", LocalDateTime.now()));
        assertFalse(statusTransaction.isFirstTransaction());
    }
}