package ua.maks.prog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelBotApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelBotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TelBotApplication.class, args);
        LOGGER.info("🚀 TelBotApplication started successfully");

        try {
            Thread.currentThread().join(); // Keeps the bot alive
        } catch (InterruptedException e) {
            LOGGER.error("🛑 Application interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // good practice
        }
    }
}
