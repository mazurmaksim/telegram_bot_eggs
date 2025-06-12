package ua.maks.prog.bot;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.maks.prog.config.BotConfig;
import ua.maks.prog.config.BotMessages;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.Order;
import ua.maks.prog.entity.Sales;
import ua.maks.prog.enums.AdminAction;
import ua.maks.prog.enums.MonthView;
import ua.maks.prog.enums.OrderStatus;
import ua.maks.prog.model.UserData;
import ua.maks.prog.service.*;

import java.time.*;
import java.util.*;

@Component
public class ChatBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);

    private final BotConfig botConfig;
    private final EggsService eggsService;
    private final CounterService counterService;
    private final SalesService salesService;
    private final BotAdminService botAdminService;
    private final OrderService orderService;
    private final BotMessages messages;
    private final Map<Long, UserData> pendingOrders = new HashMap<>();
    private final Map<Long, AdminAction> adminStates = new HashMap<>();

    public ChatBot(BotConfig botConfig, EggsService eggsService, CounterService counterService,
                   SalesService salesService, BotAdminService botAdminService, OrderService orderService,
                   BotMessages botMessages
    ) {
        this.botConfig = botConfig;
        this.eggsService = eggsService;
        this.counterService = counterService;
        this.salesService = salesService;
        this.botAdminService = botAdminService;
        this.orderService = orderService;
        this.messages = botMessages;
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
            if (update.hasCallbackQuery()) {
                LOGGER.info("Update received: userId={}, chatId={}, text={}",
                        update.getMessage().getFrom().getId(),
                        update.getMessage().getChatId(),
                        update.getMessage().getText());

                String data = update.getCallbackQuery().getData();
                Long adminChatId = update.getCallbackQuery().getMessage().getChatId();
                if (data.startsWith("complete_order:")) {
                    String phoneNumber = data.replace("complete_order:", "");
                    List<Order> order = orderService.getOrderById(UUID.fromString(phoneNumber));
                    order.forEach(o-> {
                        if (o.getStatus().getLabel().equals("New")) {
                            handleOrderCompletion(o, adminChatId);
                        }
                    });
                }

                if ("admin_back".equals(data)) {
                    sendAdminMainMenu(adminChatId);
                }
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                Long userId = update.getMessage().getFrom().getId();
                String messageText = update.getMessage().getText();
                Integer savingDateInSec = update.getMessage().getDate();
                LocalDateTime savingLocalDate = Instant.ofEpochSecond(savingDateInSec).atZone(ZoneId.of("Europe/Kyiv")).toLocalDateTime();

                boolean isAdminUser = botAdminService.getBotAdmins().stream()
                        .anyMatch(ba -> ba.getBotUserId().equals(userId));

                if (isAdminUser) {
                    handleAdminCommand(chatId, messageText, savingLocalDate, isAdminUser);
                } else {
                    handleUserCommand(chatId, messageText);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing update from chatId={}: {}", update.getMessage().getChatId(), e.getMessage(), e);
            sendMessage(update.getMessage().getChatId(),
                    messages.getAdmin().getMenu().getDataNotSaved() + e.getMessage());
        }
    }

    private void sendSaleSubMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(messages.getAdmin().getMenu().getOrder()));
        row1.add(new KeyboardButton(messages.getAdmin().getMenu().getAddToSale()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(messages.getCommon().getBack()));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(messages.getUser().getMenu().getPrompt());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error creating admin button panel for chatId {}",chatId, e);
        }
    }


    private void handleAdminCommand(Long chatId, String messageText, LocalDateTime savingLocalDate, boolean isAdmin) {
        Map<String, Runnable> commands = getStringRunnableMap(chatId);
        AdminAction state = adminStates.getOrDefault(chatId, AdminAction.NONE);
        LOGGER.debug("Admin command: chatId={}, state={}, text={}", chatId, state, messageText);

        if (state == AdminAction.WAITING_FOR_NEW_EGGS || state == AdminAction.WAITING_FOR_STOCK_INPUT) {
            try {
                int quantity = Integer.parseInt(messageText);

                if (state == AdminAction.WAITING_FOR_NEW_EGGS) {
                    saveEggCount(chatId, messageText, savingLocalDate);
                    sendMessage(chatId, messages.getAdmin().getMenu().getAdded() + quantity + messages.getCommon().getEggs());
                } else {
                    Sales sales = salesService.getAmoutToSale(LocalDate.now());
                    if (sales == null) {
                        sales = new Sales();
                    }
                    sales.setAmountToSale(quantity);
                    sales.setDateToThisAmount(LocalDate.now());
                    salesService.saveAmountToSale(sales);
                    sendMessage(chatId, messages.getAdmin().getMenu().getAddedToSale() + quantity + messages.getCommon().getEggs());
                }
                LOGGER.debug("Admin state updated: chatId={}, newState={}", chatId, AdminAction.NONE);
                adminStates.put(chatId, AdminAction.NONE);
                sendAdminMainMenu(chatId);
            } catch (NumberFormatException e) {
                sendMessage(chatId, messages.getAdmin().getMenu().getInputCorrectNum());
            }
            return;
        }

        Runnable command = commands.get(messageText);
        if (command != null) {
            command.run();
            return;
        }

        if (isAdmin) {
            sendAdminMainMenu(chatId);
        } else {
            sendMessage(chatId, messages.getAdmin().getMenu().getUnknownCommand());
        }
    }

    private void sendOrderListInline(Long chatId) {
        List<Order> newOrders = orderService.getOrderByStatus(OrderStatus.NEW);

        if (newOrders.isEmpty()) {
            sendMessage(chatId, messages.getAdmin().getMenu().getNoOrders());
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Order order : newOrders) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(messages.getAdmin().getMenu().getTelNumSign() + order.getPhoneNumber() + " :: " +
                    " " + order.getAmount() + messages.getCommon().getEggs());
            button.setCallbackData("complete_order:" + order.getId());

            rows.add(List.of(button));
        }

        InlineKeyboardButton backButton = new InlineKeyboardButton(messages.getCommon().getBack());
        backButton.setCallbackData("admin_back");
        rows.add(List.of(backButton));

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messages.getAdmin().getMenu().getSelectCompletedOrder());
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not create orders for approve for chatId {}",chatId, e);
        }
    }

    private void handleOrderCompletion(Order order, Long adminChatId) {

        if (order == null) {
            sendMessage(adminChatId, messages.getAdmin().getMenu().getNotFoundOrder());
            return;
        }

        if (OrderStatus.COMPLETED.equals(order.getStatus())) {
            sendMessage(adminChatId, messages.getAdmin().getMenu().getOrderAlreadyCompleted());
            return;
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderService.saveOrder(order);

        sendMessage(adminChatId, String.format(messages.getAdmin().getMenu().getCompletedOrder(), order.getPhoneNumber()));

        String userMsg = String.format(
                messages.getAdmin().getMenu().getYourOrderReady(),
                order.getAmount()
        );
        sendMessage(order.getChatId(), userMsg);

        sendOrderListInline(adminChatId);
        LOGGER.info("Order completed: id={}, byAdmin={}", order.getId(), adminChatId);
    }

    private void sendAdminMainMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(messages.getAdmin().getMenu().getToday()));
        row1.add(new KeyboardButton(messages.getAdmin().getMenu().getYesterday()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(messages.getAdmin().getMenu().getAddEggs()));
        row2.add(new KeyboardButton(messages.getAdmin().getMenu().getMonths()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(messages.getCommon().getToSale()));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messages.getAdmin().getMenu().getShowStatisticFrom());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error creating admin panel for chatId {}",chatId, e);
        }
    }

    private void handleUserCommand(Long chatId, String text) {
        List<Order> orders = orderService.getOrderByChatId(chatId);

        if ("/start".equals(text) || messages.getCommon().getBack().equals(text)) {
            sendUserMainMenu(chatId);
            return;
        }

        Map<String, Runnable> commandMap = Map.of(
                messages.getUser().getMenu().getMyOrders(), () -> sendUserOrderHistory(chatId),
                messages.getUser().getMenu().getDoOrder(), () -> sendQuantitySelectionMenu(chatId),
                messages.getCommon().getToSale(), () -> sendMessage(chatId, formatSalesStatistic(
                        salesService.getAmoutToSale(LocalDate.now())))
        );

        if (commandMap.containsKey(text)) {
            commandMap.get(text).run();
            return;
        }

        List<String> quantities = List.of("10", "15", "20", "30", "40", "50", "60");
        if (quantities.contains(text)) {
            int quantity = Integer.parseInt(text);
            if (orders.isEmpty()) {
                askForPhone(chatId);
                pendingOrders.put(chatId, new UserData(quantity));
            } else {
                saveOrder(chatId, new UserData(quantity), orders);
                sendConfirmation(chatId);
                sendUserMainMenu(chatId);
            }
            return;
        }

        if (pendingOrders.containsKey(chatId)) {
            String phone = text.trim();
            if (!isValidPhoneNumber(phone)) {
                sendMessage(chatId, messages.getUser().getMenu().getInvalidPhone());
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

    private void sendUserOrderHistory(Long chatId) {
        List<Order> userOrders = orderService.getOrderByChatId(chatId);

        if (userOrders.isEmpty()) {
            sendMessage(chatId, messages.getUser().getMenu().getOrderListEmpty());
            return;
        }

        StringBuilder message = new StringBuilder(messages.getUser().getMenu().getOrderListTitle()+ "\n\n");
        for (Order order : userOrders) {
            message.append(String.format(
                    messages.getUser().getMenu().getOrderAmount(),
                    order.getChatId(),
                    order.getAmount(),
                    order.getStatus() == OrderStatus.NEW ? messages.getUser().getMenu().getNewOrder() : messages.getUser().getMenu().getDoneOrder()
            ));
        }

        message.append("\n")
                .append(messages.getCommon().getBack());

        sendMessage(chatId, message.toString());
    }

    private void saveOrder(Long chatId, UserData userData, List<Order> orders) {
        String existingPhoneNumber = null;
        if(!orders.isEmpty()) {
            for (Order existingOrder : orders) {
                if (OrderStatus.NEW.equals(existingOrder.getStatus())) {
                    existingOrder.setAmount(existingOrder.getAmount() + userData.getAmount());
                    orderService.saveOrder(existingOrder);
                    sendMessage(chatId, messages.getUser().getMenu().getOrderUpdated());
                    return;
                } else {
                    existingPhoneNumber = existingOrder.getPhoneNumber();
                }
            }
        }
        Order newOrder = new Order();
        newOrder.setAmount(userData.getAmount());
        newOrder.setPhoneNumber(existingPhoneNumber ==null ?userData.getPhoneNumber():existingPhoneNumber);
        newOrder.setChatId(chatId);
        newOrder.setStatus(OrderStatus.NEW);
        orderService.saveOrder(newOrder);
        sendMessage(chatId, messages.getUser().getMenu().getOrderCreated());
    }

    private void sendUserMainMenu(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(messages.getUser().getMenu().getDoOrder()));
        row1.add(new KeyboardButton(messages.getUser().getMenu().getMyOrders()));
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(messages.getCommon().getToSale()));
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messages.getUser().getMenu().getPrompt());
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not create user menu for chatId {}", chatId, e);
        }
    }

    private void askForPhone(Long chatId) {
        SendMessage msg = new SendMessage(chatId.toString(), messages.getUser().getMenu().getAskPhone());
        msg.setParseMode("Markdown");

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            LOGGER.error("Get user phone error for chatId {}", chatId, e);
        }
    }

    private void sendConfirmation(Long chatId) {
        SendMessage msg = new SendMessage(chatId.toString(), messages.getUser().getMenu().getOrderCreated());

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            LOGGER.error("Telegram confirmation error for chatId {}", chatId, e);
        }
    }

    private void sendQuantitySelectionMenu(Long chatId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("10"), new KeyboardButton("15"), new KeyboardButton("20"))));
        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("30"), new KeyboardButton("40"), new KeyboardButton("50"))));
        keyboard.add(new KeyboardRow(List.of(new KeyboardButton("60"), new KeyboardButton(messages.getCommon().getBack()))));

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        SendMessage msg = new SendMessage(chatId.toString(), messages.getUser().getMenu().getSelectEggsAmount());
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not create amount panel {}", chatId, e);
        }
    }

    private void saveEggCount(Long chatId, String messageText, LocalDateTime savingTime) {
        eggsService.addEgg(messageText, savingTime);
        sendMessage(chatId, messages.getAdmin().getMenu().getSavedEggsAmount() + messageText);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not send message for chatId {}", chatId, e);
        }
    }

    private Map<String, Runnable> getStringRunnableMap(Long chatId) {
        List<Counter> allStatistic = counterService.getAllStatistic();

        return Map.of(
                messages.getAdmin().getMenu().getToday(), () -> sendDayAmount(chatId, LocalDate.now(), formatDayStatistic(messages.getAdmin().getMenu().getToday().toLowerCase())),
                messages.getAdmin().getMenu().getYesterday(), () -> sendDayAmount(chatId, LocalDate.now().minusDays(1), formatDayStatistic(messages.getAdmin().getMenu().getYesterday().toLowerCase())),
                messages.getAdmin().getMenu().getMonths(), () -> sendMessage(chatId, formatMonthStatistic(counterService.calculateAmountByMonth(allStatistic))),
                messages.getAdmin().getMenu().getAddedToSale(), () -> sendMessage(chatId, formatWeekStatistic(counterService.calculateAmountByWeek(allStatistic))),
                messages.getCommon().getToSale(), () -> {
                    sendMessage(chatId, formatSalesStatistic(salesService.getAmoutToSale(LocalDate.now())));
                    sendSaleSubMenu(chatId);
                },
                messages.getAdmin().getMenu().getAddToSale(), () -> {
                    sendMessage(chatId, messages.getAdmin().getMenu().getAmountToSale());
                    adminStates.put(chatId, AdminAction.WAITING_FOR_STOCK_INPUT);
                },
                messages.getAdmin().getMenu().getOrder(), () -> {
                    sendMessage(chatId, messages.getAdmin().getMenu().getOrder());
                    sendOrderListInline(chatId);
                },
                messages.getAdmin().getMenu().getAddEggs(), () -> {
                    sendMessage(chatId, messages.getAdmin().getMenu().getEnterAmountOfEggs());
                    adminStates.put(chatId, AdminAction.WAITING_FOR_NEW_EGGS);
                });
    }

    private String formatDayStatistic(String dayLabel) {
        return String.format(messages.getAdmin().getMenu().getPeriodStatistic(), dayLabel, dayLabel.substring(0, 1).toUpperCase() + dayLabel.substring(1));
    }

    private String formatMonthStatistic(Map<Month, Integer> monthStatistic) {
        StringBuilder monthBuilder = new StringBuilder(messages.getAdmin().getMenu().getStatByMonths());
        monthStatistic.forEach((month, amount) -> {
            if (amount != 0) {
                String monthName = MonthView.valueOf(month.name()).getMonthName();
                int avg = amount / month.length(true);

                monthBuilder.append(String.format("📅 %-10s : %4d 🥚  %s %3d%n",
                        monthName,
                        amount,
                        messages.getAdmin().getMenu().getAverageMonthsAmount(),
                        avg
                ));
            }
        });
        return monthBuilder.toString();
    }

    private String formatWeekStatistic(Map<Integer, Integer> monthsStatistic) {
        StringBuilder weekStatBuilder = new StringBuilder(messages.getAdmin().getMenu().getStatByWeeks());
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
                weekStatBuilder.append(messages.getAdmin().getMenu().getWeek()).append(week).append(": ").append(amount).append(" 🥚\n");
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

            String amountString = String.format(messages.getUser().getMenu().getAvailableFor(), amount);

            return dateString + System.lineSeparator() + amountString;
        } else {
            return messages.getUser().getMenu().getNotAvailableFor();
        }
    }

    public void sendDayAmount(Long chatId, LocalDate date, String message) {
        Optional<Counter> counter = counterService.getCounterByDate(date);
        counter.ifPresentOrElse(
                c -> sendMessage(chatId, message + c.getAmount() +
                        System.lineSeparator() +
                        messages.getAdmin().getMenu().getAirTemperature() +
                        c.getWeatherForecast().getTemperature() + "°C"),
                () -> sendMessage(chatId, messages.getAdmin().getMenu().getNoStatistic())
        );
    }
}
