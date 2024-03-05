package account;

import models.Account;
import models.Customer;

import java.util.Random;

import static database.BankDatabase.saveAccountToDatabase;

public class AccountHandlerImpl implements AccountHandler {

    @Override
    public Account createAccount(Customer accountOwner, double balance) {
        String accountNumber = generateAccountNumber();
        Account theAccount = new Account(accountNumber, accountOwner);
        saveAccountToDatabase(theAccount, balance); // Zapis konta do bazy danych
        return theAccount;
    }

    @Override
    public String generateAccountNumber() {
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
}
