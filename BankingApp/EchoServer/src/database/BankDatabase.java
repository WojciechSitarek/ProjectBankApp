package database;

import models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static models.Account.generateAccountNumber;

public class BankDatabase {

    public static void createCustomerAccount(String ownerID, int customerId) {
        String accountNumber = generateAccountNumber();
        double balance = 0.0;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO account (accountNumber, balance, ownerId) VALUES (?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, accountNumber);
                statement.setDouble(2, balance);
                statement.setInt(3, customerId); // Przypisanie customerId do ownerId

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

    public static String getAccountNumberByLogin(String login) {
        String accountNumber = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT accountNumber FROM Account WHERE ownerId = (SELECT customerId FROM Customer WHERE login = ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    accountNumber = resultSet.getString("accountNumber");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountNumber;
    }


    public static String getAccountInfo(String login) {
        StringBuilder accountInfo = new StringBuilder();
        String query = "SELECT Account.accountNumber, Customer.name, Customer.lastname, Customer.login, Customer.address, Customer.phoneNumber " +
                "FROM Account " +
                "INNER JOIN Customer ON Account.ownerId = Customer.customerId " +
                "WHERE Customer.login = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String accountNumber = resultSet.getString("accountNumber");
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("lastname");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phoneNumber");

                // Tworzenie stringa z informacjami o koncie
                accountInfo.append("Account number: ").append(accountNumber).append("\n");
                accountInfo.append("Name: ").append(name).append("\n");
                accountInfo.append("Lastname: ").append(lastName).append("\n");
                accountInfo.append("Address: ").append(address).append("\n");
                accountInfo.append("Phone number: ").append(phoneNumber).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountInfo.toString();
    }

    public static List<String> getTransactionHistory(String accountNumber) {
        List<String> transactionHistory = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT transactionAmount, destinationAccountNumber FROM Transaction WHERE customerAccountNumber = ?\n";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    double transactionAmount = resultSet.getDouble("Transaction.transactionAmount");
                    String destinationAccountNumber = resultSet.getString("Transaction.destinationAccountNumber");
                    String transactionInfo = "Transaction: Amount=" + transactionAmount + ", Destination Account=" + destinationAccountNumber;
                    transactionHistory.add(transactionInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionHistory;
    }



    // tutaj
    public static void makePayment(String customerAccountNumber, double depositAmount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Utwórz zapytanie SQL do aktualizacji salda na koncie
            String depositQuery = "UPDATE Account SET balance = balance + ? WHERE accountNumber = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(depositQuery)) {
                // Ustaw parametry zapytania
                preparedStatement.setDouble(1, depositAmount);
                preparedStatement.setString(2, customerAccountNumber);

                // Wykonaj zapytanie aktualizacji salda na koncie
                int rowsAffected = preparedStatement.executeUpdate();

                // Sprawdź, czy transakcja została pomyślnie zakończona
                if (rowsAffected > 0) {
                    // Zapisz wpłatę do tabeli Pay
                    String payQuery = "INSERT INTO Pay (transactionAmount, customerAccountNumber, transactionType) VALUES (?, ?, ?)";
                    try (PreparedStatement payStatement = connection.prepareStatement(payQuery)) {
                        payStatement.setDouble(1, depositAmount);
                        payStatement.setString(2, customerAccountNumber);
                        payStatement.setString(3, "deposit"); // Oznacz jako wpłatę
                        payStatement.executeUpdate();
                    }

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

        // tutuaj - wypłata
        public static void makePaycheck(String accountNumber, double transactionAmount) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Sprawdzamy, czy istnieje odpowiedni rekord w tabeli Account
                if (isValidAccount(connection, accountNumber)) {
                    // Sprawdzamy, czy na koncie jest wystarczająca ilość środków
                    if (hasSufficientBalance(connection, accountNumber, transactionAmount)) {
                        // Dokonujemy wypłaty
                        performPayment(connection, accountNumber, transactionAmount);
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

        private static boolean isValidAccount(Connection connection, String accountNumber) throws SQLException {
            String query = "SELECT * FROM Account WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, accountNumber);
                return preparedStatement.executeQuery().next();
            }
        }

        private static boolean hasSufficientBalance(Connection connection, String accountNumber, double transactionAmount) throws SQLException {
            String query = "SELECT balance FROM Account WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, accountNumber);
                double currentBalance = 0;
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    currentBalance = resultSet.getDouble("balance");
                }
                return currentBalance >= transactionAmount;
            }
        }

        private static void performPayment(Connection connection, String accountNumber, double transactionAmount) throws SQLException {
            String updateQuery = "UPDATE Account SET balance = balance - ? WHERE accountNumber = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setDouble(1, transactionAmount);
                updateStatement.setString(2, accountNumber);
                updateStatement.executeUpdate();
            }

            String insertQuery = "INSERT INTO Pay (transactionAmount, customerAccountNumber, transactionType) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setDouble(1, transactionAmount);
                insertStatement.setString(2, accountNumber);
                insertStatement.setString(3, "withdraw"); // Oznacz jako wypłatę
                insertStatement.executeUpdate();
            }
        }
    }

// tutaj - przelew
public static void makeTransfer(String accountNumber, String destinationAccountNumber, double transferAmount) {
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Sprawdź, czy środki na koncie źródłowym są wystarczające
        if (checkSufficientFunds(connection, accountNumber, transferAmount)) {
            // Rozpocznij transakcję
            connection.setAutoCommit(false);

            try {
                // Zmniejsz saldo na koncie źródłowym
                updateBalance(connection, accountNumber, -transferAmount);

                // Zwiększ saldo na koncie docelowym
                updateBalance(connection, destinationAccountNumber, transferAmount);

                // Zapisz transakcję w tabeli Transaction
                saveTransaction(connection, accountNumber, destinationAccountNumber, transferAmount);

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

    private static void saveTransaction(Connection connection, String accountNumber, String destinationAccountNumber, double transactionAmount) throws SQLException {
        String insertQuery = "INSERT INTO Transaction (transactionAmount, customerAccountNumber, destinationAccountNumber) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setDouble(1, transactionAmount);
            insertStatement.setString(2, accountNumber);
            insertStatement.setString(3, destinationAccountNumber);
            insertStatement.executeUpdate();
        }
    }

    private static boolean checkSufficientFunds(Connection connection, String accountNumber, double amount) throws SQLException {
        String query = "SELECT balance FROM Account WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    return balance >= amount;
                }
            }
        }
        return false;
    }

    private static void updateBalance(Connection connection, String accountNumber, double amount) throws SQLException {
        String query = "UPDATE Account SET balance = balance + ? WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, accountNumber);
            preparedStatement.executeUpdate();
        }
    }

}


