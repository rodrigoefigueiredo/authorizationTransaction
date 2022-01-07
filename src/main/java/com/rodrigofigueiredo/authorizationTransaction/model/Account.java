package com.rodrigofigueiredo.authorizationTransaction.model;

import java.util.List;

public class Account {
    private boolean active;
    private double availableLimit;
    private List<Transaction> history;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(double availableLimit) {
        this.availableLimit = availableLimit;
    }

    public List<Transaction> getHistory() {
        return history;
    }

    public void setHistory(List<Transaction> history) {
        this.history = history;
    }

    public Account(boolean active, double availableLimit, List<Transaction> history) {
        this.active = active;
        this.availableLimit = availableLimit;
        this.history = history;
    }

    public Account() {
    }

    public boolean isFirstTransaction () {
        return (this.getHistory() == null || (this.getHistory() !=null && this.getHistory().isEmpty()));
    }
}
