package com.discordchatbot;

import com.discordchatbot.dto.QuoteDTO;
import com.discordchatbot.response.ChattingReaction;
import com.discordchatbot.response.QuoteController;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class ChattingReactionTest {

    @Mock
    private QuoteController quoteController;

    @InjectMocks
    private ChattingReaction chattingReaction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void manualTest() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        MessageCreateAction action = mock(MessageCreateAction.class);

        when(user.isBot()).thenReturn(false);
        when(event.getAuthor()).thenReturn(user);  // 추가된 부분
        when(event.getMessage()).thenReturn(message);
        when(message.getContentRaw()).thenReturn("any input");
        when(message.reply(any(CharSequence.class))).thenReturn(action);

        chattingReaction.onMessageReceived(event);

        verify(action).queue();
    }

    @Test
    public void quoteTest() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        MessageCreateAction action = mock(MessageCreateAction.class);

        when(user.isBot()).thenReturn(false);
        when(event.getAuthor()).thenReturn(user);  // 추가된 부분
        when(event.getMessage()).thenReturn(message);
        when(message.getContentRaw()).thenReturn("/명언");
        when(message.reply(any(CharSequence.class))).thenReturn(action);

        QuoteDTO quote = new QuoteDTO();
        quote.setContent("Test Quote");
        quote.setAuthor("Test Author");
        when(quoteController.getQuote()).thenReturn(quote);

        chattingReaction.onMessageReceived(event);

        verify(action).queue();
    }

    @Test
    public void testAddQuoteOnCommand() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        MessageCreateAction action = mock(MessageCreateAction.class);

        when(user.isBot()).thenReturn(false);
        when(event.getAuthor()).thenReturn(user);  // 추가된 부분
        when(event.getMessage()).thenReturn(message);
        when(message.getContentRaw()).thenReturn("/명언추가 TestAuthor TestQuote");
        when(message.reply(any(CharSequence.class))).thenReturn(action);

        try (MockedStatic<DatabaseManager> databaseManagerMock = mockStatic(DatabaseManager.class)) {
            databaseManagerMock.when(() -> DatabaseManager.addQuote("TestAuthor", "TestQuote")).thenReturn(true);

            chattingReaction.onMessageReceived(event);

            verify(action).queue();
        }
    }

    @Test
    public void testRandomQuoteFromDatabase() {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        MessageCreateAction action = mock(MessageCreateAction.class);

        when(user.isBot()).thenReturn(false);
        when(event.getAuthor()).thenReturn(user);  // 추가된 부분
        when(event.getMessage()).thenReturn(message);
        when(message.getContentRaw()).thenReturn("/명언");
        when(message.reply(any(CharSequence.class))).thenReturn(action);

        QuoteDTO quote = new QuoteDTO();
        quote.setContent("Database Quote");
        quote.setAuthor("Database Author");

        try (MockedStatic<DatabaseManager> databaseManagerMock = mockStatic(DatabaseManager.class)) {
            databaseManagerMock.when(DatabaseManager::getRandomQuote).thenReturn(quote);

            chattingReaction.onMessageReceived(event);

            verify(action).queue();
        }
    }
}
