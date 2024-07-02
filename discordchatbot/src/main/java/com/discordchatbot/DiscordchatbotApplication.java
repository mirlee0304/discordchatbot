package com.discordchatbot;

import com.discordchatbot.response.ChattingReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.EnumSet;

public class DiscordchatbotApplication {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		BotTokenManager tokenManager = context.getBean(BotTokenManager.class);

		EnumSet<GatewayIntent> intents = EnumSet.of(
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_VOICE_STATES);
		String token = tokenManager.getBotToken();
		JDA jda = net.dv8tion.jda.api.JDABuilder.createDefault(token)
				.enableIntents(intents)
				.addEventListeners(new ChattingReaction())
				.build();
	}
}
