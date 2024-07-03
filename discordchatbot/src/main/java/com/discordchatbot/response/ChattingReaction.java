package com.discordchatbot.response;

import com.discordchatbot.dto.QuoteDTO;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChattingReaction extends ListenerAdapter {

    private final QuoteController quoteController;

    @Autowired
    public ChattingReaction(QuoteController quoteController) {
        this.quoteController = quoteController;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        QuoteDTO quote = quoteController.getQuote();
        if (quote != null && quote.getContent() != null && !quote.getContent().isEmpty()) {
            event.getMessage().reply(quote.getContent() + " - " + quote.getAuthor()).queue();
        } else {
            event.getMessage().reply("명언을 가져올 수 없습니다.").queue();
        }
    }
}
