package ua.maks.prog.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.maks.prog.config.BotConfig;

@Component
public class ChatBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public ChatBot(BotConfig botConfig) {
        super();
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            System.out.println("New message from " + chatId + ": " + messageText);
        }
    }
}