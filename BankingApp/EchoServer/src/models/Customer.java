package models;

public class Customer {

    // pola: name, lastName, login, password, address, customerId, phoneNumber;

    // konstruktor1 - do logowania: login , hasło

    // konstrukotr2 - tworzenie użytkownika: name, lastName,login, password, address, CustomerId, phoneNumber;

    // gettery

    // toString()


    private String name;
    private String lastname;
    private String login;
    private String password;
    private String address;
    private int customerId;
    private int phoneNumber;
    private static int nextId;




//    public Customer(String login, String password){
//        this.login = login;
//        this.password = password;
//    }
//    public Customer(String name, String lastname, int phoneNumber, String address){
//
//    }



    


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static int getNextId() { return nextId; }
}

