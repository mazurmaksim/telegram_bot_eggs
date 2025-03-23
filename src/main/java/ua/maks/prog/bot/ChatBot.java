package ua.maks.prog.bot;

import org.knowm.xchart.AnnotationText;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.maks.prog.config.BotConfig;
import ua.maks.prog.service.EggsService;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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

                switch (messageText) {
                    case "Сьогодні" :
                        sendMessage(chatId, "Показую статистику за сьогодні");
                        sendTodayStats(chatId);
                        break;
                }

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

    private void sendTodayStats(Long chatId) {
        LocalDate today = LocalDate.now();
        List<Integer> xData = new ArrayList<>();
        List<Integer> yData = new ArrayList<>();

        for (int i = 1; i <= today.getDayOfMonth(); i++) {
            xData.add(i);
            yData.add((int) (Math.random() * 10));
        }

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Статистика за " + today.getMonth().name())
                .xAxisTitle("День місяця")
                .yAxisTitle("Кількість яєць")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setMarkerSize(6);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setXAxisLabelRotation(45);

        chart.addSeries("Яйця", xData, yData);

        AnnotationText textAnnotation = new AnnotationText(
                today.getMonth().name(),
                today.getDayOfMonth() / 2.0,
                yData.stream().mapToInt(Integer::intValue).max().orElse(10) + 2,
                true
        );
        chart.addAnnotation(textAnnotation);

        File chartFile = new File("chart.png");
        try {
            BitmapEncoder.saveBitmap(chart, chartFile.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Сталася помилка при створенні графіка.");
            return;
        }


        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(chartFile));

        try {
            execute(sendPhoto);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Сталася помилка при відправці графіка.");
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
