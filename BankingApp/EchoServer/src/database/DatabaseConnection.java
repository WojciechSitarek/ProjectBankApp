package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdatabase";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin1234";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found");
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = getConnection();
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to database.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
