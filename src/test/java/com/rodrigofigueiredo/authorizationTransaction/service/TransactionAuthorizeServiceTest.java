package com.rodrigofigueiredo.authorizationTransaction.service;

import com.rodrigofigueiredo.authorizationTransaction.model.Account;
import com.rodrigofigueiredo.authorizationTransaction.model.Transaction;
import com.rodrigofigueiredo.authorizationTransaction.model.TransactionAuthorizeResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionAuthorizeServiceTest {

    @Autowired
    private TransactionAuthorizeService transactionAuthorizeService;
    @Test
    public void validateTransactionTest(){
        Transaction transaction = new Transaction(10, "Test1", LocalDateTime.now());
        Account account = new Account(true, 100, new ArrayList<Transaction>());
        TransactionAuthorizeResponseBody response = transactionAuthorizeService.authorize(transaction, account);
        assertTrue(response.getViolations().isEmpty());
        transaction = new Transaction(91, "Test1", LocalDateTime.now());
        account = new Account(true, 100, new ArrayList<Transaction>());
        response = transactionAuthorizeService.authorize(transaction, account);
        assertFalse(response.getViolations().isEmpty());
        assertEquals(response.getViolations().size(), 1);
        assertEquals(response.getViolations().get(0), "first-transaction-above-threshold");
        transaction = new Transaction(10, "Burger King", LocalDateTime.now());
        account = new Account(true, 100, null);
        response = transactionAuthorizeService.authorize(transaction, account);

    }

    @Test
    public void validateAmountFirstTransactionTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transaction transaction = new Transaction(10, "Burger King", LocalDateTime.now());
        Account account = new Account(true, 100, null);
        Method methodIsValidAmountFirstTransaction = TransactionAuthorizeService.class
                .getDeclaredMethod("isValidAmountFirstTransaction", Transaction.class, Account.class);
        methodIsValidAmountFirstTransaction.setAccessible(true);
        boolean isValidAmountFirstTransaction = (boolean) methodIsValidAmountFirstTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        assertTrue(isValidAmountFirstTransaction);
        transaction.setAmount(90.0001);
        account.setAvailableLimit(100);
        isValidAmountFirstTransaction = (boolean) methodIsValidAmountFirstTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        assertFalse(isValidAmountFirstTransaction);
    }

    @Test
    public void hasSufficientLimitTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transaction transaction = new Transaction(99.99999, "Burger King", LocalDateTime.now());
        Account account = new Account(true, 100, null);
        Method methodHasSufficientLimit = TransactionAuthorizeService.class
                .getDeclaredMethod("hasSufficientLimit", Transaction.class, Account.class);
        methodHasSufficientLimit.setAccessible(true);
        boolean hasSufficientLimit = (boolean) methodHasSufficientLimit
                .invoke(transactionAuthorizeService, transaction, account);
        assertTrue(hasSufficientLimit);
        transaction.setAmount(100.00001);
        hasSufficientLimit = (boolean) methodHasSufficientLimit
                .invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasSufficientLimit);
    }

    @Test
    public void hasHighFrequencySmallIntervalTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transaction transaction = new Transaction(10, "Test", LocalDateTime.of(2021, 12, 15, 10, 30, 00));
        Account account = new Account(true, 100, null);
        Method method = TransactionAuthorizeService.class.getDeclaredMethod("hasHighFrequencySmallInterval", Transaction.class, Account.class);
        method.setAccessible(true);
        boolean hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.setHistory(new ArrayList<Transaction>());
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(300)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(6000000)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(400)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(3000)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusDays(300)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusHours(300)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusMinutes(50)));
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(600)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(28, transaction.getMerchant(), transaction.getTime().minusSeconds(60)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(70, "Test2", transaction.getTime().minusSeconds(119)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(30, "Test3", transaction.getTime().minusSeconds(1190000)));
        account.getHistory().add(new Transaction(45, transaction.getMerchant(), transaction.getTime().minusSeconds(1222)));
        account.getHistory().add(new Transaction(40, "Test2", transaction.getTime().minusSeconds(1100000)));
        account.getHistory().add(new Transaction(30, transaction.getMerchant(), transaction.getTime().minusSeconds(130)));
        account.getHistory().add(new Transaction(25, transaction.getMerchant(), transaction.getTime().minusSeconds(121)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(55, transaction.getMerchant(), transaction.getTime().minusSeconds(110)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(25, transaction.getMerchant(), transaction.getTime().minusSeconds(114)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(25, transaction.getMerchant(), transaction.getTime().minusSeconds(1224)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasHighFrequencySmallInterval);
        account.getHistory().add(new Transaction(25, transaction.getMerchant(), transaction.getTime().minusSeconds(110)));
        hasHighFrequencySmallInterval = (boolean) method.invoke(transactionAuthorizeService, transaction, account);
        assertTrue(hasHighFrequencySmallInterval);
    }

    @Test
    public void hasDoubledTransactionTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transaction transaction = new Transaction(10, "Test", LocalDateTime.now());
        Account account = new Account(true, 100, new ArrayList<Transaction>());
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(119)));
        Method methodHasDoubledTransaction = TransactionAuthorizeService.class
                .getDeclaredMethod("hasDoubledTransaction", Transaction.class, Account.class);
        methodHasDoubledTransaction.setAccessible(true);
        boolean hasDoubledTransaction = (boolean) methodHasDoubledTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        account.getHistory().clear();
        assertTrue(hasDoubledTransaction);
        account.setHistory(new ArrayList<Transaction>());
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(120)));
        hasDoubledTransaction = (boolean) methodHasDoubledTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        assertTrue(hasDoubledTransaction);
        transaction = new Transaction(10, "Test", LocalDateTime.now());
        account.setHistory(new ArrayList<Transaction>());
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusSeconds(121)));
        hasDoubledTransaction = (boolean) methodHasDoubledTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasDoubledTransaction);
        transaction = new Transaction(10, "Test", LocalDateTime.now());
        account.setHistory(new ArrayList<Transaction>());
        account.getHistory().add(new Transaction(transaction.getAmount(), transaction.getMerchant(), transaction.getTime().minusHours(1)));
        hasDoubledTransaction = (boolean) methodHasDoubledTransaction
                .invoke(transactionAuthorizeService, transaction, account);
        assertFalse(hasDoubledTransaction);
    }

    @Test
    public void test() throws InterruptedException {
        Transaction t1 = new Transaction(10, "Test", LocalDateTime.now());
        Transaction t2 = new Transaction(20, t1.getMerchant(), t1.getTime().plusMinutes(3));
        Transaction t3 = new Transaction(30, t1.getMerchant(), t1.getTime().minusMinutes(5));
        List<Transaction> list = new ArrayList<>();
        list.add(t2);
        list.add(t3);
        list.add(t1);
        Collections.sort(list);

        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusMinutes(3);
        Duration d1 = Duration.between(time1, time2);
        System.out.println(d1.abs());


        System.out.println(list);
    }
}