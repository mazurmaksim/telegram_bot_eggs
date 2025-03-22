package ua.maks.prog.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.maks.prog.config.BotConfig;
import ua.maks.prog.service.EggsService;

@Component
public class ChatBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final EggsService eggsService;

    public ChatBot(BotConfig botConfig, EggsService eggsService) {
        super();
        this.botConfig = botConfig;
        this.eggsService = eggsService;
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
        Long chatId = update.getMessage().getChatId();
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                eggsService.addEgg(messageText);
                System.out.println("New message from " + chatId + ": " + messageText);
                sendMessage(chatId, "Кількість яєць збережена: " + messageText);
            }
        } catch (Exception e) {
            sendMessage(chatId, "Дані не збереглись, повинно бути число " + e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}