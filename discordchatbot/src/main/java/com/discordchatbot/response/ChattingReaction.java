package com.discordchatbot.response;

import com.discordchatbot.DatabaseManager;
import com.discordchatbot.dto.QuoteDTO;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChattingReaction extends ListenerAdapter {

    private final QuoteController quoteController;
    String manualMessage = "안녕하세요! 저는 랜덤 명언 출력 봇입니다. 사용법은 다음과 같습니다:\n" +
            "1. '/명언' 명령어를 사용하여 랜덤 명언을 받습니다.\n" +
            "2. '/명언추가 author quote' 명령어를 사용하여 명언을 추가할 수 있습니다.\n" +
            "3. 채팅에 아무거나 입력하여 사용법을 다시 확인할 수 있습니다.\n";

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
            QuoteDTO quote = quoteController.getQuote();
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
        if (event.getUser().isBot()) {
            return;
        }

        if (event.getNewOnlineStatus() == OnlineStatus.ONLINE) {
            event.getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage(manualMessage).queue();
            });
        }
    }
}
