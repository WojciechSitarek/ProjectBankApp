package models;

import java.util.ArrayList;
import java.util.List;

public class Account {

    // Pola:  accountId, balance, Customer accountOwner, List<Transaction> transactionHistory

    // Konstruktor: accountId, balance, Customer accountOwner,

    // gettery i settery: getter na 100%, settery zobaczymy

    //toString()

    private String accountNumber;
    private double balance;
    private Customer accountOwner;
    private List<Transaction> transactionHistory = new ArrayList<>();


    public Account(String accountNumber, Customer accountOwner) {
        this.accountNumber = accountNumber;
        this.accountOwner = accountOwner;
        this.balance = 0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(Customer accountOwner) {
        this.accountOwner = accountOwner;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public void getFullAccountInfo() {
        String info = String.format("Customer [Account number: %s, Balance: %s, Account Owner: %s]",
                this.accountNumber, this.balance, this.accountOwner);
        System.out.println(info);
    }
}
