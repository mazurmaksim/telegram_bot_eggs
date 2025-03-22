package ua.maks.prog.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.maks.prog.config.BotConfig;
import ua.maks.prog.service.EggsService;

import java.util.ArrayList;
import java.util.List;

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

                if (messageText.equals("/start")) {
                    sendMainMenu(update.getMessage());
                } else {
                    eggsService.addEgg(messageText);
                    System.out.println("New message from " + chatId + ": " + messageText);
                    sendMessage(chatId, "Кількість яєць збережена: " + messageText);
                }
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

    private void sendMainMenu(Message message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Сьогодні"));
        row1.add(new KeyboardButton("Дні(поточний місяць)"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Тижні(Поточний місяць)"));
        row2.add(new KeyboardButton("Роки"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Місяці"));
        row3.add(new KeyboardButton("Меню 6"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Показати статистику за:");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
