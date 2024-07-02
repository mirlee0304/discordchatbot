package com.discordchatbot.response;

import com.discordchatbot.DatabaseManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChattingReaction extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String str = event.getMessage().getContentDisplay();

        if (str != null && !str.isEmpty()) {
            String randomQuote = DatabaseManager.getRandomQuote();
            if (randomQuote != null) {
                event.getMessage().reply(randomQuote).queue();
            } else {
                event.getMessage().reply("명언을 가져올 수 없습니다.").queue();
            }
        }
    }
}
