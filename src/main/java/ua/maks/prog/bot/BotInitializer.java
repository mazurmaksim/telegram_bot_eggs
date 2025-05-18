package ua.maks.prog.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final ChatBot bot;

    public BotInitializer(ChatBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ Bot init started");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("✅ Bot successfully registered");
        } catch (Exception e) {
            System.err.println("❌ Bot registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

