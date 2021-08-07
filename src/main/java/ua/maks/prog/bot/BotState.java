package ua.maks.prog.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public enum BotState {
    Start {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Hello! its a private Maks Mazur chat Bot");
        }

        @Override
        public BotState nextState() {
            return EnterPhone;
        }
    },

    EnterPhone {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Enter your phone number please:");
        }

        @Override
        public void handleInput(BotContext context) {
            String phoneNumber = context.getInput();

            if(Utils.isValidPhoneNumber(phoneNumber)) {
                context.getUser().setPhone(phoneNumber);
                next = EnterEmail;
            } else {
                sendMessage(context, "Wrong phone number!");
                next = EnterPhone;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    EnterEmail {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Enter your e-mail please:");
        }

        @Override
        public void handleInput(BotContext context) {
            String email = context.getInput();

            if(Utils.isValidEmailAddress(email)) {
                context.getUser().setEmail(context.getInput());
                next = Approved;
            } else {
                sendMessage(context, "Wrong e-mail addres!");
                next = EnterEmail;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    Approved(false) {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Thank you for application!");
        }

        @Override
        public BotState nextState() {
            return Start;
        }
    };

    private static BotState[] states;
    private final boolean inputNeeded;

    BotState() {
        this.inputNeeded = true;
    }

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static BotState getInitialState() {
        return byId(0);
    }

    public static BotState byId(int id) {
        if(states == null) {
            states = BotState.values();
        }
        return states[id];
    }

    protected void sendMessage(BotContext context, String text) {
        SendMessage message = new SendMessage()
                .setChatId(context.getUser().getChatId())
                .setText(text);
        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void handleInput(BotContext context){

    }

    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public abstract void enter(BotContext context);
    public abstract BotState nextState();
}
