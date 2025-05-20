package ua.maks.prog.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);
    private final ChatBot chatBot;

    public BotInitializer(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    @Override
    public void run(String... args) {
        LOGGER.info("Bot init started");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(chatBot);
            LOGGER.info("Bot successfully registered");
        } catch (Exception e) {
            LOGGER.error("Bot registration failed: {}", e.getMessage(), e);
        }
    }
}



