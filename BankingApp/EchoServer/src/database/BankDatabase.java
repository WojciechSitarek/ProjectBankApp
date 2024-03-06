package database;

import models.Account;
import models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BankDatabase {
    private static final String CHECK_ACCOUNT_EXISTS_QUERY = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";

    public static boolean accountExists(String accountNumber) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_ACCOUNT_EXISTS_QUERY)) {
            statement.setString(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveAccountToDatabase(Account account, double initialBalance) {
        String INSERT_ACCOUNT_QUERY = "INSERT INTO accounts (account_number, balance, customer_id) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT_QUERY)) {
            statement.setString(1, account.getAccountNumber());
            statement.setDouble(2, initialBalance);
            statement.setInt(3, account.getAccountOwner().getCustomerId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerCustomer() {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("===== Rejestracja klienta =====");
            System.out.print("Imię: ");
            String name = scanner.nextLine();

            System.out.print("Nazwisko: ");
            String lastname = scanner.nextLine();

            System.out.print("Login: ");
            String login = scanner.nextLine();

            System.out.print("Hasło: ");
            String password = scanner.nextLine();

            System.out.print("Adres: ");
            String address = scanner.nextLine();

            System.out.print("Numer telefonu: ");
            int phoneNumber = Integer.parseInt(scanner.nextLine());

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

    public static Customer loginUser() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Podaj login:");
        String login = scanner.nextLine();

        System.out.println("Podaj hasło:");
        String password = scanner.nextLine();

        Customer user = getUserFromDatabase(login, password);

        // Sprawdź, czy użytkownik istnieje i czy podane hasło jest poprawne
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Zalogowano pomyślnie!");
            return user;
        } else {
            System.out.println("Nieudane logowanie. Spróbuj ponownie.");
            return loginUser(); // Rekurencyjne wywołanie metody w przypadku nieudanego logowania
        }
    }

    public static Customer getUserFromDatabase(String login, String password) throws SQLException {
        String query = "SELECT * FROM Customer WHERE login = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Utwórz obiekt Customer na podstawie wyników zapytania
                Customer user = new Customer();
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                return user;
            } else {
                return null; // Zwróć null, jeśli użytkownik o podanym loginie i haśle nie został znaleziony
            }
        }
    }

    public static void main(String[] args) {
        registerCustomer();
    }
}
