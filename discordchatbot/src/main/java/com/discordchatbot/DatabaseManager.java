package com.discordchatbot;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/quotes";
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private static Connection getConnection() throws SQLException {
        int retries = 0;
        while (true) {
            try {
                return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            } catch (SQLException e) {
                if (++retries > MAX_RETRIES) {
                    throw e;
                }
                logger.warn("Failed to connect to the database. Retrying... ({} out of {})", retries, MAX_RETRIES);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Database connection retries interrupted", interruptedException);
                }
            }
        }
    }

    public static String getRandomQuote() {
        String randomQuote = null;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT quote, author FROM motivational_quotes ORDER BY RAND() LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String quote = resultSet.getString("quote");
                String author = resultSet.getString("author");
                randomQuote = String.format("\"%s\" - %s", quote, author);
            }

        } catch (SQLException e) {
            logger.error("Failed to retrieve random quote", e);
        }
        return randomQuote;
    }

    public static boolean addQuote(String author, String quote) {
        String insertQuoteSQL = "INSERT INTO motivational_quotes (author, quote) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuoteSQL)) {

            preparedStatement.setString(1, author);
            preparedStatement.setString(2, quote);
            int affectedRows = preparedStatement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Failed to add quote", e);
            return false;
        }
    }
}
