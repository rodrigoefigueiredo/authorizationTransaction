package com.rodrigofigueiredo.authorizationTransaction.model;


import java.time.LocalDateTime;
import java.util.Date;

public class Transaction implements Comparable<Transaction>{
    private double amount;
    private String merchant;
    private LocalDateTime time;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Transaction(double amount, String merchant, LocalDateTime time) {
        this.amount = amount;
        this.merchant = merchant;
        this.time = time;
    }

    @Override
    public int compareTo(Transaction o) {
        if (this.time.isBefore(o.getTime())){
            return -1;
        } else if(this.time.isAfter(o.getTime())) {
            return 1;
        }
        return 0;
    }
}
