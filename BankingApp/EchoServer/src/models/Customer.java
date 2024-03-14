package models;

public class Customer {
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

    public static int getNextId() { return nextId; }
}

