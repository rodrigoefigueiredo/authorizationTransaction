package com.rodrigofigueiredo.authorizationTransaction.service;

import com.rodrigofigueiredo.authorizationTransaction.exceptions.TransactionAuthorizationException;
import com.rodrigofigueiredo.authorizationTransaction.model.Account;
import com.rodrigofigueiredo.authorizationTransaction.model.Transaction;
import com.rodrigofigueiredo.authorizationTransaction.model.TransactionAuthorizeResponseBody;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionAuthorizeService {
    public TransactionAuthorizeResponseBody authorize(Transaction transaction, Account account) {
        List<TransactionAuthorizationException> violations = validateTransaction(transaction, account);
        TransactionAuthorizeResponseBody response;
        List<String> violationsArray = new ArrayList<String>();
        if (violations.isEmpty()) {
            account = processTransaction(transaction, account);
        } else {
            violationsArray = violations
                    .stream()
                    .map(TransactionAuthorizationException::getMessage)
                    .collect(Collectors.toList());
        }
        response = new TransactionAuthorizeResponseBody(account, violationsArray);

        return response;
    }

    private Account processTransaction(Transaction transaction, Account account) {
        account.setAvailableLimit(account.getAvailableLimit() - transaction.getAmount());
        if (account.getHistory() == null || (account.getHistory() != null && account.getHistory().isEmpty())) {
            account.setHistory(new ArrayList<Transaction>());
        }
        account.getHistory().add(transaction);
        return account;
    }

    public List<TransactionAuthorizationException> validateTransaction(Transaction transaction, Account account) {
        List<TransactionAuthorizationException> violations = new ArrayList<TransactionAuthorizationException>();
        if (account.getHistory() == null || (account.getHistory() != null && account.getHistory().isEmpty())) {
            account.setHistory(new ArrayList<Transaction>());
        }
        if (!account.isActive()) {
            violations.add(new TransactionAuthorizationException("account-not-active"));
        }
        if (!hasSufficientLimit(transaction, account)) {
            violations.add(new TransactionAuthorizationException("insufficient-limit"));
        }
        if (!isValidAmountFirstTransaction(transaction, account)) {
            violations.add(new TransactionAuthorizationException("first-transaction-above-threshold"));
        }
        if (hasDoubledTransaction(transaction, account)) {
            violations.add(new TransactionAuthorizationException("doubled-transaction"));
        }
        return violations;
    }

    private boolean isValidAmountFirstTransaction(Transaction transaction, Account account) throws TransactionAuthorizationException {
        return account.isFirstTransaction() && !(transaction.getAmount() > account.getAvailableLimit() * 0.9);
    }

    private boolean hasSufficientLimit(Transaction transaction, Account statusTransaction) {
        return transaction.getAmount() <= statusTransaction.getAvailableLimit();
    }

    private boolean hasHighFrequencySmallInterval(Transaction transaction, Account account) {
        Map<String, Integer> highFrequency = new HashMap<String, Integer>();
        if (!account.isFirstTransaction()){
            Collections.sort(account.getHistory());
            for (Transaction t :
                    account.getHistory()) {
                if (Duration.between(t.getTime(), transaction.getTime()).getSeconds() <= 120L) {
                    if (!highFrequency.containsKey(t.getMerchant())) {
                        highFrequency.put(t.getMerchant(), new Integer(1));
                    } else {
                        Integer integer = new Integer(highFrequency.get(t.getMerchant()).intValue() + 1);
                        highFrequency.replace(t.getMerchant(), integer);
                        if (highFrequency.get(t.getMerchant()).intValue() > 3) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasDoubledTransaction(Transaction transaction, Account account) {
        if (!account.isFirstTransaction()){
            Collections.sort(account.getHistory());
            for (Transaction t :
                    account.getHistory()) {
                if (transaction.getAmount() == t.getAmount() && transaction.getMerchant() == t.getMerchant()
                        && Duration.between(t.getTime(), transaction.getTime()).getSeconds() <= 120L) {
                    return true;
                }
            }
        }
        return false;
    }
}