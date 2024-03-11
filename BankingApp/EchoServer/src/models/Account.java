package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Account {

    // Pola:  accountId, balance, Customer accountOwner, List<Transaction> transactionHistory

    // Konstruktor: accountId, balance, Customer accountOwner,

    // gettery i settery: getter na 100%, settery zobaczymy

    //toString()

    private String accountNumber;
    private double balance;
    private int customerId;
    private List<Transaction> transactionHistory = new ArrayList<>();


    public Account(String accountNumber, double balance, int customerId) {
        this.accountNumber = generateAccountNumber();
        this.balance = balance;
        this.customerId = customerId;
    }

    public Account() {

    }
    public static String generateAccountNumber() {
        try {
            Random random = new Random();
            StringBuilder accountNumber = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                int digit = random.nextInt(10);
                accountNumber.append(digit);
            }
            return accountNumber.toString();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance(int cutomerId) {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public void getFullAccountInfo() {
        String info = String.format("Customer [Account number: %s, Balance: %s, OwnerId: %s]",
                this.accountNumber, this.balance, this.customerId);
        System.out.println(info);
    }
}
