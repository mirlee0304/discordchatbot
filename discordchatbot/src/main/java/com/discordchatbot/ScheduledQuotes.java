package com.discordchatbot;

import com.discordchatbot.dto.QuoteDTO;
import com.discordchatbot.response.QuoteController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduledQuotes {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledQuotes.class);

    @Autowired
    private QuoteController quoteController;

    @Autowired
    private JDA jda;

    @Scheduled(fixedRate = 600000)
    public void reportCurrentTime() {
        logger.info("Scheduled task triggered");
        try {
            QuoteDTO quote = quoteController.getQuote();
            String message = "\"" + quote.getContent()+"\"" +'\n' + "-" + quote.getAuthor();

            TextChannel generalChannel = jda.getTextChannelsByName("일반", true).stream().findFirst().orElse(null);
            if (generalChannel != null) {
                logger.info("Sending message to channel: {}", generalChannel.getName());
                generalChannel.sendMessage(message).queue();
            } else {
                logger.warn("No channel named '일반' found.");
            }
        } catch (Exception e) {
            logger.error("Failed to send scheduled quote", e);
        }
    }
}
