package com.discordchatbot;

import com.discordchatbot.response.ChattingReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.EnumSet;

@Configuration
@ComponentScan(basePackages = "com.discordchatbot")
public class AppConfig {

    @Bean
    public BotTokenManager botTokenManager() {
        return new BotTokenManager();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public JDA jda(ChattingReaction chattingReaction, BotTokenManager tokenManager) throws Exception {
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES);

        String token = tokenManager.getBotToken();
        return net.dv8tion.jda.api.JDABuilder.createDefault(token)
                .enableIntents(intents)
                .addEventListeners(chattingReaction)
                .build();
    }
}
