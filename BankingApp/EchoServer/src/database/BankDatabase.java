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


    public static void payment(int customerAccountNumber, double depositAmount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Utwórz zapytanie SQL do aktualizacji salda na koncie
            String depositQuery = "UPDATE Account SET balance = balance + ? WHERE ownerId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(depositQuery)) {
                // Ustaw parametry zapytania
                preparedStatement.setDouble(1, depositAmount);
                preparedStatement.setInt(2, customerAccountNumber);

                // Wykonaj zapytanie
                int rowsAffected = preparedStatement.executeUpdate();

                // Sprawdź, czy transakcja została pomyślnie zakończona
                if (rowsAffected > 0) {
                    System.out.println("Wpłata pomyślnie zrealizowana.");
                } else {
                    System.out.println("Wpłata nieudana. Sprawdź numer konta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public class PaymentProcessor {

        public static void makePayment(int customerAccountNumber, double transactionAmount) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Sprawdzamy, czy istnieje odpowiedni rekord w tabeli Account
                if (isValidAccount(connection, customerAccountNumber)) {
                    // Sprawdzamy, czy na koncie jest wystarczająca ilość środków
                    if (hasSufficientBalance(connection, customerAccountNumber, transactionAmount)) {
                        // Dokonujemy wypłaty
                        performPayment(connection, customerAccountNumber, transactionAmount);
                        System.out.println("Wypłata zakończona pomyślnie.");
                    } else {
                        System.out.println("Brak wystarczających środków na koncie.");
                    }
                } else {
                    System.out.println("Nieprawidłowy numer konta.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static boolean isValidAccount(Connection connection, int customerAccountNumber) throws SQLException {
            String query = "SELECT * FROM Account WHERE ownerId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerAccountNumber);
                return preparedStatement.executeQuery().next();
            }
        }

        private static boolean hasSufficientBalance(Connection connection, int customerAccountNumber, double transactionAmount) throws SQLException {
            String query = "SELECT balance FROM Account WHERE ownerId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerAccountNumber);
                double currentBalance = 0;
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    currentBalance = resultSet.getDouble("balance");
                }
                return currentBalance >= transactionAmount;
            }
        }

        private static void performPayment(Connection connection, int customerAccountNumber, double transactionAmount) throws SQLException {
            String updateQuery = "UPDATE Account SET balance = balance - ? WHERE ownerId = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setDouble(1, transactionAmount);
                updateStatement.setInt(2, customerAccountNumber);
                updateStatement.executeUpdate();
            }

            String insertQuery = "INSERT INTO Pay (transactionAmount, customerAccountNumber) VALUES (?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setDouble(1, transactionAmount);
                insertStatement.setInt(2, customerAccountNumber);
                insertStatement.executeUpdate();
            }
        }
    }


    public static void makeTransfer(int customerAccountNumber, int destinationAccountNumber, double transferAmount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Sprawdź, czy środki na koncie źródłowym są wystarczające
            if (checkSufficientFunds(connection, customerAccountNumber, transferAmount)) {
                // Rozpocznij transakcję
                connection.setAutoCommit(false);

                try {
                    // Zmniejsz saldo na koncie źródłowym
                    updateBalance(connection, customerAccountNumber, -transferAmount);

                    // Zwiększ saldo na koncie docelowym
                    updateBalance(connection, destinationAccountNumber, transferAmount);

                    // Zatwierdź transakcję
                    connection.commit();

                    System.out.println("Przelew pomyślnie zrealizowany.");
                } catch (SQLException e) {
                    // W razie błędu anuluj transakcję
                    connection.rollback();
                    System.out.println("Błąd podczas przetwarzania przelewu. Transakcja anulowana.");
                } finally {
                    // Przywróć domyślną funkcję automatycznego zatwierdzania
                    connection.setAutoCommit(true);
                }
            } else {
                System.out.println("Niewystarczające środki na koncie źródłowym.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkSufficientFunds(Connection connection, int accountNumber, double amount) throws SQLException {
        String query = "SELECT balance FROM Account WHERE ownerId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountNumber);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    return balance >= amount;
                }
            }
        }
        return false;
    }

    private static void updateBalance(Connection connection, int accountNumber, double amount) throws SQLException {
        String query = "UPDATE Account SET balance = balance + ? WHERE ownerId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, accountNumber);
            preparedStatement.executeUpdate();
        }
    }

}


