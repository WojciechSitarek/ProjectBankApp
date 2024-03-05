package account;

import models.Account;
import models.Customer;


public interface AccountHandler {

    Account createAccount(Customer accountOwner, double balance);

    String generateAccountNumber();

}
