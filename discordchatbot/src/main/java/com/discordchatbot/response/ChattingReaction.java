package com.discordchatbot.response;

import com.discordchatbot.DatabaseManager;
import com.discordchatbot.dto.QuoteDTO;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class ChattingReaction extends ListenerAdapter {

    private final QuoteController quoteController;
    public String manualMessage = "안녕하세요! 저는 랜덤 명언 출력 봇입니다. 10분마다 랜덤 명언을 출력합니다. 사용법은 다음과 같습니다:\n" +
            "1. '/명언' 명령어를 사용하여 랜덤 명언을 받습니다.\n" +
            "2. '/명언추가 author quote' 명령어를 사용하여 명언을 추가할 수 있습니다.\n" +
            "3. 채팅에 아무거나 입력하여 사용법을 다시 확인할 수 있습니다.\n";

    private static final Logger logger = LoggerFactory.getLogger(ChattingReaction.class);

    @Autowired
    public ChattingReaction(QuoteController quoteController) {
        this.quoteController = quoteController;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String message = event.getMessage().getContentRaw();

        if (message.startsWith("/명언추가 ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                event.getMessage().reply("사용법: /명언추가 author quote 형식으로 명언을 추가하세요.").queue();
            } else {
                String author = parts[1];
                String quote = parts[2];
                boolean success = DatabaseManager.addQuote(author, quote);
                if (success) {
                    event.getMessage().reply("명언이 성공적으로 추가되었습니다.").queue();
                } else {
                    event.getMessage().reply("명언 추가에 실패했습니다. 다시 시도해 주세요.").queue();
                }
            }
        } else if ("/명언".equals(message)) {
            Random random = new Random();
            int num = random.nextInt(6);
            QuoteDTO quote;
            if (num <= 4) {
                quote = quoteController.getQuote();
            } else {
                quote = DatabaseManager.getRandomQuote();
            }

            if (quote != null && quote.getContent() != null && !quote.getContent().isEmpty()) {
                event.getMessage().reply("\"" + quote.getContent() + "\"" + '\n' + "-" + quote.getAuthor()).queue();
            } else {
                event.getMessage().reply("명언을 가져올 수 없습니다.").queue();
            }
        } else {
            event.getMessage().reply(manualMessage).queue();
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        logger.info("User online status updated: {}", event.getUser().getName());
        Guild guild = event.getGuild();

        if (guild == null) {
            logger.warn("Guild is null for user: {}", event.getUser().getName());
            return;
        }

        List<Member> users = guild.getMembers();
        logger.info("Retrieved {} members from guild: {}", users.size(), guild.getName());

        for (Member member : users) {
            logger.info("Checking member: {}", member.getEffectiveName());
            if (!member.getUser().isBot() && member.getOnlineStatus() == OnlineStatus.ONLINE) {
                TextChannel channel = guild.getTextChannelById("1257540439490301984");
                if (channel != null) {
                    logger.info("Sending message to channel: {}", channel.getName());
                    channel.sendMessage(manualMessage).queue();
                } else {
                    logger.warn("Text channel not found: 1257540439490301984");
                }
            }
        }
    }
}
