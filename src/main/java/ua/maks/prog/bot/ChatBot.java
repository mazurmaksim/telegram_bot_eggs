package ua.maks.prog.bot;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
import ua.maks.prog.entity.BotAdmin;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.Order;
import ua.maks.prog.entity.Sales;
import ua.maks.prog.service.*;
import ua.maks.prog.views.MonthView;
import ua.maks.prog.views.OrderStatus;

import java.time.*;
import java.util.*;

@Component
public class ChatBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final EggsService eggsService;
    private final CounterService counterService;
    private final SalesService salesService;
    private final BotAdminService botAdminService;
    private final OrderService orderService;
    private final Map<Long, Integer> pendingOrders = new HashMap<>();

    public ChatBot(BotConfig botConfig, EggsService eggsService, CounterService counterService,
                   SalesService salesService, BotAdminService botAdminService, OrderService orderService
    ) {
        this.botConfig = botConfig;
        this.eggsService = eggsService;
        this.counterService = counterService;
        this.salesService = salesService;
        this.botAdminService = botAdminService;
        this.orderService = orderService;
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
                Long userId = update.getMessage().getFrom().getId();
                String messageText = update.getMessage().getText();
                Integer savingDateInSec = update.getMessage().getDate();
                LocalDateTime savingLocalDate = Instant.ofEpochSecond(savingDateInSec).atZone(ZoneId.of("Europe/Kyiv")).toLocalDateTime();

                boolean isAdminUser = botAdminService.getBotAdmins().stream()
                        .anyMatch(ba -> ba.getBotUserId().equals(userId));

                if (isAdminUser) {
                    handleAdminCommand(chatId, messageText, savingLocalDate, update);
                } else {
                    handleUserCommand(chatId, messageText);
                }
            }
        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), "Дані не збереглись, повинно бути число: " + e.getMessage());
        }
    }

    private void handleAdminCommand(Long chatId, String messageText, LocalDateTime savingLocalDate, Update update) {
        Map<String, Runnable> commands = getStringRunnableMap(chatId);

        if ("/start".equals(messageText)) {
            sendAdminMainMenu(update.getMessage());
        } else if (commands.containsKey(messageText)) {
            commands.get(messageText).run();
        } else {
            saveEggCount(chatId, messageText, savingLocalDate);
        }
    }

    private void sendAdminMainMenu(Message message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Сьогодні"));
        row1.add(new KeyboardButton("Вчора"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Тижні(Поточний місяць)"));
        row2.add(new KeyboardButton("Місяці"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("На продаж"));

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

    private void handleUserCommand(Long chatId, String text) {
        switch (text) {
            case "Зробити замовлення":
                sendQuantitySelectionMenu(chatId);
                break;
            case "10":
            case "15":
            case "20":
            case "30":
            case "40":
            case "50":
            case "60":
                pendingOrders.put(chatId, Integer.parseInt(text));
                askForNameAndPhone(chatId);
                break;
            default:
                if (pendingOrders.containsKey(chatId)) {
                    saveOrder(chatId, pendingOrders.get(chatId), text);
                    pendingOrders.remove(chatId);
                    sendConfirmation(chatId);
                } else {
                    sendUserMainMenu(chatId);
                }
        }
    }

    public static boolean isValidPhoneNumber(String phone) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "UA");
            return phoneUtil.isValidNumber(numberProto);
        } catch (Exception e) {
            return false;
        }
    }

    private void saveOrder(Long chatId, int quantity, String contactInfo) {
        String name;
        String phoneNumber;
        String[] userInfo = contactInfo.split(",");

        if (userInfo.length == 2 && isValidPhoneNumber(userInfo[1])) {
            name = userInfo[0].trim();
            phoneNumber = userInfo[1].trim();
        } else {
            sendMessage(chatId, "❌ Некоректні контактні дані. Введіть у форматі: Ім'я,Телефон");
            return;
        }

        List<Order> ordersByPhone = orderService.getOrderByPhone(phoneNumber);

        boolean updated = false;

        for (Order existingOrder : ordersByPhone) {
            if (OrderStatus.NEW.equals(existingOrder.getStatus())) {
                existingOrder.setAmount(existingOrder.getAmount() + quantity);
                orderService.saveOrder(existingOrder);
                updated = true;
                break;
            }
        }

        if (!updated) {
            Order newOrder = new Order();
            newOrder.setAmount(quantity);
            newOrder.setPhoneNumber(phoneNumber);
            newOrder.setUserName(name);
            newOrder.setStatus(OrderStatus.NEW);
            orderService.saveOrder(newOrder);
        }

        System.out.printf("✅ Замовлення: %d яєць від %d, контакт: %s%n", quantity, chatId, contactInfo);
    }


    private void sendUserMainMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("На продаж"));
        row1.add(new KeyboardButton("Зробити замовлення"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("В наявності:");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void askForNameAndPhone(Long chatId) {
        SendMessage msg = new SendMessage(chatId.toString(), "Введіть ваше ім'я та номер телефону у форматі:\n\n**Ім'я, телефон**");
        msg.setParseMode("Markdown");

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendConfirmation(Long chatId) {
        SendMessage msg = new SendMessage(chatId.toString(), "✅ Дякуємо! Ваше замовлення прийнято. Очікуйте дзвінка.");

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendQuantitySelectionMenu(Long chatId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("10"), new KeyboardButton("15"), new KeyboardButton("20"))));
        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("30"), new KeyboardButton("40"), new KeyboardButton("50"))));
        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("60"), new KeyboardButton("Повернутись назад"))));

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        SendMessage msg = new SendMessage(chatId.toString(), "🥚 Оберіть кількість яєць:");
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void saveEggCount(Long chatId, String messageText, LocalDateTime savingTime) {
        eggsService.addEgg(messageText, savingTime);
        sendMessage(chatId, "💾 Кількість яєць збережена: " + messageText);
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

    private Map<String, Runnable> getStringRunnableMap(Long chatId) {
        List<Counter> allStatistic = counterService.getAllStatistic();

        Map<String, Runnable> commands = Map.of(
                "Сьогодні", () -> sendDayAmount(chatId, LocalDate.now(), formatDayStatistic("сьогодні")),
                "Вчора", () -> sendDayAmount(chatId, LocalDate.now().minusDays(1), formatDayStatistic("вчора")),
                "Місяці", () -> sendMessage(chatId, formatMonthStatistic(counterService.calculateAmountByMonth(allStatistic))),
                "Тижні(Поточний місяць)", () -> sendMessage(chatId, formatWeekStatistic(counterService.calculateAmountByWeek(allStatistic))),
                "На продаж", () -> sendMessage(chatId, formatSalesStatistic(salesService.getAmoutToSale(LocalDate.now())))
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
                weekStatBuilder.append("🗓 Тиждень ").append(week).append(": ").append(amount).append(" 🥚\n");
            }
        });

        return weekStatBuilder.toString();
    }

    private String formatSalesStatistic(Sales sales) {
        if (sales != null && sales.getAmountToSale() > 0) {
            LocalDate date = sales.getDateToThisAmount();
            int amount = sales.getAmountToSale();

            String dateString = String.format("\uD83D\uDCC5 %d %s %d",
                    date.getDayOfMonth(),
                    MonthView.valueOf(date.getMonth().name()).getMonthName(),
                    date.getYear()
            );

            String amountString = String.format("\uD83E\uDD5A Доступно для продажу: %d шт.", amount);

            return dateString + System.lineSeparator() + amountString;
        } else {
            return "\uD83D\uDE1E🥚 Нажаль, на продаж немає яєць 🥚\uD83D\uDE1E";
        }
    }

    public void sendDayAmount(Long chatId, LocalDate date, String message) {
        Optional<Counter> counter = counterService.getCounterByDate(date);
        counter.ifPresentOrElse(
                c -> sendMessage(chatId, message + c.getAmount() +
                        System.lineSeparator() +
                        "\uD83C\uDF21\uFE0F  Температура повітря: " +
                        c.getWeatherForecast().getTemperature() + "°C"),
                () -> sendMessage(chatId, "На сьогодні немає збереженої статистики")
        );
    }
}
