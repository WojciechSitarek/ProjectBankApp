package models;

public class Transaction {

    //pola: transactionAmount, CustomerAccountNumber, destinationAccountNumber, transactionId

    // konstruktor:  sourceAccountNumber,  destinationAccountNumber, transactionAmount

    // gettery - metody coś ala getInfo.

    //toString - transactionInfo.


    private double transactionAmount;
    private int customerAccountNumber;
    private int destinationAccountNumber;
    private int transactionId;


    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }



    public int getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(int customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }



    public int getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(int destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }



    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }



    public Transaction(int customerAccountNumber, int destinationAccountNumber, double transactionAmount){
        this.customerAccountNumber = customerAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.transactionAmount = transactionAmount;
    }



    public void getTransactionInfo() {
        String transInfo = String.format("Customer [Account number: %s, Amount: %s, Destination Account number: %s, TransactionId: %s]",
                this.customerAccountNumber, this.transactionAmount,this.destinationAccountNumber, this.transactionId);
        System.out.println(transInfo);
    }

}
