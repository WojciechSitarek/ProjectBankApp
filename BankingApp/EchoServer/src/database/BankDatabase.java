package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankDatabase {
    private static final String GET_BALANCE_QUERY = "SELECT balance FROM accounts WHERE account_number = ?";
    private static final String UPDATE_BALANCE_QUERY = "UPDATE accounts SET balance = ? WHERE account_number = ?";
    private static final String CHECK_ACCOUNT_EXISTS_QUERY = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";

    public static double getBalance(String accountNumber) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BALANCE_QUERY)) {
            statement.setString(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // zwróć -1, jeśli konto nie istnieje lub wystąpił błąd
    }

    public static boolean deposit(String accountNumber, double amount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            double currentBalance = getBalance(accountNumber);
            if (currentBalance >= 0) {
                double newBalance = currentBalance + amount;
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE_QUERY)) {
                    statement.setDouble(1, newBalance);
                    statement.setString(2, accountNumber);
                    return statement.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean withdraw(String accountNumber, double amount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            double currentBalance = getBalance(accountNumber);
            if (currentBalance >= 0 && currentBalance >= amount) {
                double newBalance = currentBalance - amount;
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE_QUERY)) {
                    statement.setDouble(1, newBalance);
                    statement.setString(2, accountNumber);
                    return statement.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean transfer(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            double sourceBalance = getBalance(sourceAccountNumber);
            double destinationBalance = getBalance(destinationAccountNumber);
            if (sourceBalance >= 0 && sourceBalance >= amount) {
                double newSourceBalance = sourceBalance - amount;
                double newDestinationBalance = destinationBalance + amount;
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE_QUERY)) {
                    // Aktualizacja stanu konta źródłowego
                    statement.setDouble(1, newSourceBalance);
                    statement.setString(2, sourceAccountNumber);
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE_QUERY)) {
                    // Aktualizacja stanu konta docelowego
                    statement.setDouble(1, newDestinationBalance);
                    statement.setString(2, destinationAccountNumber);
                    statement.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
}
