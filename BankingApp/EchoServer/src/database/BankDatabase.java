package database;

import models.Account;
import models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static models.Account.generateAccountNumber;

public class BankDatabase {

    public static void createCustomerAccount(String ownerID) {

        String accountNumber = generateAccountNumber();
        double balance = 0.0;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO account (accountNumber, balance, ownerID) VALUES (?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, accountNumber);
                statement.setDouble(2, balance);
                statement.setString(3, ownerID);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Konto zostało utworzone pomyślnie!");
                } else {
                    System.out.println("Błąd podczas tworzenia konta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerCustomer(String name, String lastname, String login, String password, String address, int phoneNumber) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO customer (name, lastname, login, password, address, phoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.setString(2, lastname);
                statement.setString(3, login);
                statement.setString(4, password);
                statement.setString(5, address);
                statement.setInt(6, phoneNumber);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Klient został zarejestrowany pomyślnie!");
                } else {
                    System.out.println("Błąd podczas rejestracji klienta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getCustomerIdByLogin(String login) {
        int customerId = 0;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT customerId FROM customer WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    customerId = resultSet.getInt("customerId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerId;
    }

    public static boolean checkCredentials(String login, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM customer WHERE login = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, login);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next(); // Zwróci true, jeśli istnieje wiersz z takim loginem i hasłem
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static double getBalanceByLogin(String login) {
        double balance = 0.0;
        int customerId = getCustomerIdByLogin(login); // Pobranie identyfikatora klienta na podstawie loginu

        // Zapytanie do bazy danych
        String query = "SELECT balance FROM Account WHERE ownerId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            // Jeśli istnieją wyniki, pobierz stan konta
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balance;
    }

    public static String getAccountInfo(int ownerId) throws SQLException {
        StringBuilder accountInfo = new StringBuilder();
        String query = "SELECT * FROM Account WHERE ownerId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String accountNumber = resultSet.getString("accountNumber");
                int balance = resultSet.getInt("balance");
                Account account = new Account(accountNumber, balance, ownerId);
                accountInfo.append(account.toString()).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountInfo.toString();
    }
}


