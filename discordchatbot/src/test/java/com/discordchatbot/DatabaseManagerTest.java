// DatabaseManagerTest.java
package com.discordchatbot;

import com.discordchatbot.dto.QuoteDTO;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseManagerTest {

    private static final String MOCK_JDBC_URL = "jdbc:mysql://localhost:3306/quotes";
    private static final String MOCK_DB_USER = "testuser";
    private static final String MOCK_DB_PASSWORD = "testpassword";

    @BeforeEach
    public void setUp() {
        // Mock environment variables
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            Dotenv dotenv = mock(Dotenv.class);
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);

            when(dotenv.get("DB_USER")).thenReturn(MOCK_DB_USER);
            when(dotenv.get("DB_PASSWORD")).thenReturn(MOCK_DB_PASSWORD);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilder);
        }
    }

    @Test
    public void testGetRandomQuote() throws SQLException {
        QuoteDTO expectedQuote = new QuoteDTO();
        expectedQuote.setContent("Test Quote");
        expectedQuote.setAuthor("Test Author");

        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("quote")).thenReturn("Test Quote");
        when(mockResultSet.getString("author")).thenReturn("Test Author");

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {

            Dotenv dotenv = mock(Dotenv.class);
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);

            when(dotenv.get("DB_USER")).thenReturn(MOCK_DB_USER);
            when(dotenv.get("DB_PASSWORD")).thenReturn(MOCK_DB_PASSWORD);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilder);

            driverManagerMock.when(() -> DriverManager.getConnection(MOCK_JDBC_URL, MOCK_DB_USER, MOCK_DB_PASSWORD)).thenReturn(mockConnection);

            QuoteDTO actualQuote = DatabaseManager.getRandomQuote();

            assertNotNull(actualQuote);
            assertEquals(expectedQuote.getContent(), actualQuote.getContent());
            assertEquals(expectedQuote.getAuthor(), actualQuote.getAuthor());
        }
    }

    @Test
    public void testAddQuote() throws SQLException {
        String author = "Test Author";
        String quote = "Test Quote";

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {

            Dotenv dotenv = mock(Dotenv.class);
            DotenvBuilder dotenvBuilder = mock(DotenvBuilder.class);

            when(dotenv.get("DB_USER")).thenReturn(MOCK_DB_USER);
            when(dotenv.get("DB_PASSWORD")).thenReturn(MOCK_DB_PASSWORD);
            when(dotenvBuilder.load()).thenReturn(dotenv);
            dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilder);

            driverManagerMock.when(() -> DriverManager.getConnection(MOCK_JDBC_URL, MOCK_DB_USER, MOCK_DB_PASSWORD)).thenReturn(mockConnection);

            boolean result = DatabaseManager.addQuote(author, quote);

            assertTrue(result);
        }
    }
}
