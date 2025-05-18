package ua.maks.prog.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.maks.prog.TelBotApplication;

@Component
public class BotInitializer implements CommandLineRunner {

    private final ChatBot chatBot;

    public BotInitializer(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    @Override
    public void run(String... args) {
        System.out.println("✅ Bot init started");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(chatBot);
            System.out.println("✅ Bot successfully registered");
        } catch (Exception e) {
            System.err.println("❌ Bot registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



