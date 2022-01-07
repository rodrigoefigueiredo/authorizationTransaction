package com.rodrigofigueiredo.authorizationTransaction.model;

import java.util.ArrayList;
import java.util.List;

public class TransactionAuthorizeResponseBody {
    private Account account;
    private List<String> violations = new ArrayList<String>();

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<String> getViolations() {
        return violations;
    }

    public void setViolations(List<String> violations) {
        this.violations = violations;
    }

    public TransactionAuthorizeResponseBody(Account account, List<String> violations) {
        this.account = account;
        this.violations = violations;
    }
}
