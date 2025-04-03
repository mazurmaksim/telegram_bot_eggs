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
import ua.maks.prog.entity.Counter;
import ua.maks.prog.service.CounterService;
import ua.maks.prog.service.EggsService;
import ua.maks.prog.views.MonthView;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

@Component
public class ChatBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final EggsService eggsService;
    private final CounterService counterService;

    public ChatBot(BotConfig botConfig, EggsService eggsService, CounterService counterService) {
        super();
        this.botConfig = botConfig;
        this.eggsService = eggsService;
        this.counterService = counterService;
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
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                String messageText = update.getMessage().getText();
                Map<String, Runnable> commands = getStringRunnableMap(chatId);

                if ("/start".equals(messageText)) {
                    sendMainMenu(update.getMessage());
                } else if (commands.containsKey(messageText)) {
                    commands.get(messageText).run();
                } else {
                    saveEggCount(chatId, messageText);
                }
            }
        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), "Дані не збереглись, повинно бути число " + e.getMessage());
        }
    }

    private Map<String, Runnable> getStringRunnableMap(Long chatId) {
        List<Counter> allStatistic = counterService.getAllStatistic();

        Map<String, Runnable> commands = Map.of(
                "Сьогодні", () -> sendDayAmount(chatId, LocalDate.now(), formatDayStatistic("сьогодні")),
                "Вчора", () -> sendDayAmount(chatId, LocalDate.now().minusDays(1), formatDayStatistic("вчора")),
                "Місяці", () -> sendMessage(chatId, formatMonthStatistic(counterService.calculateAmountByMonth(allStatistic))),
                "Тижні(Поточний місяць)", () -> sendMessage(chatId, formatWeekStatistic(counterService.calculateAmountByWeek(allStatistic)))
        );
        return commands;
    }

    private String formatDayStatistic(String dayLabel) {
        return String.format("📊 Показую статистику за %s\n📅 %s яєць: ", dayLabel, dayLabel.substring(0, 1).toUpperCase() + dayLabel.substring(1));
    }

    private String formatMonthStatistic(Map<Month, Integer> monthStatistic) {
        StringBuilder monthBuilder = new StringBuilder("📊 Статистика по місяцям:\n\n");
        monthStatistic.forEach((month, amount) -> {
            if (amount != 0) {
                monthBuilder.append("📅 ")
                        .append(MonthView.valueOf(month.name()).getMonthName())
                        .append(": ")
                        .append(amount)
                        .append(" 🥚\n");
            }
        });
        return monthBuilder.toString();
    }

    private String formatWeekStatistic(Map<Integer, Integer> monthsStatistic) {
        StringBuilder weekStatBuilder = new StringBuilder("📊 Показую статистику за тижні:\n\n");
        Map<Integer, Integer> weeksStatistic = new TreeMap<>();

        YearMonth currentMonth = YearMonth.now();
        int daysInMonth = currentMonth.lengthOfMonth();
        int firstDayOfWeek = currentMonth.atDay(1).getDayOfWeek().getValue();

        for (int day = 1; day <= daysInMonth; day++) {
            int weekNumber = (day + firstDayOfWeek - 2) / 7;
            weeksStatistic.merge(weekNumber, monthsStatistic.getOrDefault(day, 0), Integer::sum);
        }

        weeksStatistic.forEach((week, amount) -> {
            if (week != 0 && amount != 0) {
                weekStatBuilder.append("🗓 Тижні ").append(week).append(": ").append(amount).append(" 🥚\n");
            }
        });

        return weekStatBuilder.toString();
    }

    private void saveEggCount(Long chatId, String messageText) {
        eggsService.addEgg(messageText);
        System.out.println("New message from " + chatId + ": " + messageText);
        sendMessage(chatId, "\uD83D\uDCBE Кількість яєць збережена: " + messageText);
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

    public void sendDayAmount(Long chatId, LocalDate date, String message) {
        Optional<Counter> counter = counterService.getCounterByDate(date);
        counter.ifPresentOrElse(
                c -> sendMessage(chatId, message + c.getAmount() +
                        System.lineSeparator() +
                        "\uD83C\uDF21\uFE0F  Температура повітря: " +
                        c.getWeatherForecast().getTemperature()),
                () -> sendMessage(chatId, "На сьогодні немає збереженої статистики")
        );
    }

    private void sendMainMenu(Message message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Сьогодні"));
        row1.add(new KeyboardButton("Вчора"));

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
