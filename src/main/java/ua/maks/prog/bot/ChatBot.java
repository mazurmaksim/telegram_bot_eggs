package ua.maks.prog.bot;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.maks.prog.config.BotConfig;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.Order;
import ua.maks.prog.entity.Sales;
import ua.maks.prog.model.UserData;
import ua.maks.prog.service.*;
import ua.maks.prog.views.AdminAction;
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
    private final Map<Long, UserData> pendingOrders = new HashMap<>();
    private final Map<Long, AdminAction> adminStates = new HashMap<>();

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
                    handleAdminCommand(chatId, messageText, savingLocalDate);
                } else {
                    handleUserCommand(chatId, messageText);
                }
            }
        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), "Дані не збереглись, повинно бути число: " + e.getMessage());
        }
    }

    private void sendSaleSubMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Замовлення"));
        row1.add(new KeyboardButton("Додати на продаж"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("⬅ Назад"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Оберіть дію:");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void handleAdminCommand(Long chatId, String messageText, LocalDateTime savingLocalDate) {
        Map<String, Runnable> commands = getStringRunnableMap(chatId);
        AdminAction state = adminStates.getOrDefault(chatId, AdminAction.NONE);

        switch (messageText) {
            case "На продаж":
                sendSaleSubMenu(chatId);
                return;
            case "Додати на продаж":
                sendMessage(chatId, "Введіть кількість яєць для продажу:");
                adminStates.put(chatId, AdminAction.WAITING_FOR_STOCK_INPUT);
                return;
            case "Додати яйця":
                sendMessage(chatId, "Введіть кількість яєць:");
                adminStates.put(chatId, AdminAction.WAITING_FOR_NEW_EGGS);
                return;
            case "/start":
            case "⬅ Назад":
                adminStates.put(chatId, AdminAction.NONE);
                sendAdminMainMenu(chatId);
                return;
        }

        if (state == AdminAction.WAITING_FOR_NEW_EGGS || state == AdminAction.WAITING_FOR_STOCK_INPUT) {
            try {
                int quantity = Integer.parseInt(messageText);

                if (state == AdminAction.WAITING_FOR_NEW_EGGS) {
                    saveEggCount(chatId, messageText, savingLocalDate);
                    sendMessage(chatId, "✅ Додано " + quantity + " яєць.");
                } else {
                    Sales sales = salesService.getAmoutToSale(LocalDate.now());
                    if (sales == null) {
                        sales = new Sales();
                    }
                    sales.setAmountToSale(quantity);
                    sales.setDateToThisAmount(LocalDate.now());
                    salesService.saveAmountToSale(sales);
                    sendMessage(chatId, "✅ Додано на продаж " + quantity + " яєць.");
                }

                adminStates.put(chatId, AdminAction.NONE);
                sendAdminMainMenu(chatId);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "❗ Введіть коректне число. Наприклад: 30");
            }
            return;
        }

        if (commands.containsKey(messageText)) {
            adminStates.put(chatId, AdminAction.NONE);
            commands.get(messageText).run();
            return;
        }

        sendMessage(chatId, "⚠️ Невідома команда або формат. Спробуйте ще раз.");
    }


    private void sendAdminMainMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Сьогодні"));
        row1.add(new KeyboardButton("Вчора"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Додати яйця"));
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
        sendMessage.setChatId(chatId);
        sendMessage.setText("Показати статистику за:");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleUserCommand(Long chatId, String text) {
        List<Order> orders = orderService.getOrderByChatId(chatId);

        if ("/start".equals(text) || "⬅ Назад".equals(text)) {
            sendUserMainMenu(chatId);
            return;
        }

        switch (text) {
            case "Зробити замовлення":
                sendQuantitySelectionMenu(chatId);
                return;

            case "10":
            case "15":
            case "20":
            case "30":
            case "40":
            case "50":
            case "60":
                int quantity = Integer.parseInt(text);
                if (orders.isEmpty()) {
                    askForPhone(chatId);
                    pendingOrders.put(chatId, new UserData(quantity));
                } else {
                    saveOrder(chatId, new UserData(quantity), orders);
                    sendConfirmation(chatId);
                }
                return;

            case "На продаж":
                String stat = formatSalesStatistic(salesService.getAmoutToSale(LocalDate.now()));
                sendMessage(chatId, stat);
                return;
        }

        if (pendingOrders.containsKey(chatId)) {
            String phone = text.trim();

            if (!isValidPhoneNumber(phone)) {
                sendMessage(chatId, "❌ Невірний формат номера. Введіть у форматі: +380XXXXXXXXX");
                return;
            }

            UserData userData = pendingOrders.get(chatId);
            userData.setPhoneNumber(phone);

            saveOrder(chatId, userData, orders);
            pendingOrders.remove(chatId);
            sendConfirmation(chatId);
            return;
        }

        sendUserMainMenu(chatId);
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

    private void saveOrder(Long chatId, UserData userData, List<Order> orders) {
        String phoneNumber = null;

        for (Order existingOrder : orders) {
            if (OrderStatus.NEW.equals(existingOrder.getStatus())) {
                existingOrder.setAmount(existingOrder.getAmount() + userData.getAmount());
                orderService.saveOrder(existingOrder);
                sendMessage(chatId, "✅ Ваше поточне замовлення оновлено.");
                return;
            }
        }
        Order newOrder = new Order();
        newOrder.setAmount(userData.getAmount());
        newOrder.setPhoneNumber(userData.getPhoneNumber());
        newOrder.setChatId(chatId);
        newOrder.setStatus(OrderStatus.NEW);
        orderService.saveOrder(newOrder);
        sendMessage(chatId, "✅ Нове замовлення прийнято. Дякуємо!");
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
        sendMessage.setText("Оберіть пункт нижче");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void askForPhone(Long chatId) {
        SendMessage msg = new SendMessage(chatId.toString(), "Введіть телефону:");
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
        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("60"), new KeyboardButton("⬅ Назад"))));

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

        return Map.of(
                "Сьогодні", () -> sendDayAmount(chatId, LocalDate.now(), formatDayStatistic("сьогодні")),
                "Вчора", () -> sendDayAmount(chatId, LocalDate.now().minusDays(1), formatDayStatistic("вчора")),
                "Місяці", () -> sendMessage(chatId, formatMonthStatistic(counterService.calculateAmountByMonth(allStatistic))),
                "Тижні(Поточний місяць)", () -> sendMessage(chatId, formatWeekStatistic(counterService.calculateAmountByWeek(allStatistic))),
                "На продаж", () -> sendMessage(chatId, formatSalesStatistic(salesService.getAmoutToSale(LocalDate.now())))
        );
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
