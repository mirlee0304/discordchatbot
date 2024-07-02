package com.discordchatbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.discordchatbot")
public class AppConfig {

    @Bean
    public BotTokenManager botTokenManager() {
        return new BotTokenManager();
    }
}
