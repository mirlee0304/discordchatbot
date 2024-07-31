package com.discordchatbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class BotTokenManager {
    private String botToken;

    public BotTokenManager() {
        settingToken();
    }

    private void settingToken() {
        // Dotenv를 사용하여 환경 변수를 로드
        Dotenv dotenv = Dotenv.configure().load();
        botToken = dotenv.get("BOT_TOKEN");

        if (botToken == null || botToken.isEmpty()) {
            throw new RuntimeException("Bot token is not set in the environment variables.");
        } else {
            System.out.println("BOT_TOKEN successfully loaded from .env file.");
        }
    }
    public String getBotToken() {
        return botToken;
    }
}
