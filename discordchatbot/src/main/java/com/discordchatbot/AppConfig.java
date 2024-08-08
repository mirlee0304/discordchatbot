package com.discordchatbot;

import com.discordchatbot.response.ChattingReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;

@Configuration
@ComponentScan(basePackages = "com.discordchatbot")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Bean
    public BotTokenManager botTokenManager() {
        return new BotTokenManager();
    }

    @Bean
    public JDA jda(ChattingReaction chattingReaction, BotTokenManager tokenManager) throws Exception {
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_PRESENCES);

        String token = tokenManager.getBotToken();
        int retries = 0;
        while (true) {
            try {
                return net.dv8tion.jda.api.JDABuilder.createDefault(token)
                        .enableIntents(intents)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .enableCache(CacheFlag.ONLINE_STATUS)
                        .addEventListeners(chattingReaction)
                        .build();
            } catch (Exception e) {
                if (++retries > MAX_RETRIES) {
                    logger.error("Failed to initialize JDA after {} retries", retries, e);
                    throw e;
                }
                logger.warn("Failed to initialize JDA. Retrying... ({} out of {})", retries, MAX_RETRIES);
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }
}
