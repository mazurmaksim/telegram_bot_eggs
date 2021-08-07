package ua.maks.prog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class TelBotApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TelBotApplication.class, args);

    }
}
