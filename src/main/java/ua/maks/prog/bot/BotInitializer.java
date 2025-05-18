package ua.maks.prog.bot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer implements CommandLineRunner {

    private final ChatBot bot;

    public BotInitializer(ChatBot bot) {
        this.bot = bot;
    }

    @Override
    public void run(String... args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("✅ Telegram bot successfully started!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
