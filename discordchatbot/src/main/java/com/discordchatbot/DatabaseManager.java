package com.discordchatbot;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/quotes";
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    public static String getRandomQuote() {
        String randomQuote = null;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT quote, author FROM motivational_quotes ORDER BY RAND() LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String quote = resultSet.getString("quote");
                String author = resultSet.getString("author");
                randomQuote = String.format("\"%s\" - %s", quote, author);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return randomQuote;
    }
}
