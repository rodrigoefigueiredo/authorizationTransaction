package com.rodrigofigueiredo.authorizationTransaction.api;

import com.rodrigofigueiredo.authorizationTransaction.model.Account;
import com.rodrigofigueiredo.authorizationTransaction.model.Transaction;
import com.rodrigofigueiredo.authorizationTransaction.model.TransactionAuthorizeResponseBody;
import com.rodrigofigueiredo.authorizationTransaction.service.TransactionAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class TransactionAuthorization {

    @Autowired
    private TransactionAuthorizeService transactionAuthorizeService;

    @PostMapping("/api/v1/authorize")
    public TransactionAuthorizeResponseBody authorize(@RequestBody RequestBodyTransaction body) {
        System.out.println(body.getTransaction());
        return transactionAuthorizeService.authorize(body.getTransaction(), body.getAccount());
    }

    static class RequestBodyTransaction {
        private Transaction transaction;
        private  Account account;

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public RequestBodyTransaction() {
        }

        public RequestBodyTransaction(Transaction transaction, Account account) {
            this.transaction = transaction;
            this.account = account;
        }
    }
}
