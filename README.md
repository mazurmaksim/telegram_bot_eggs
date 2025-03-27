# Telegram Bot for Egg Statistics

This is a **Telegram bot** designed to complement the [Egg Statistics JavaFX application](https://github.com/mazurmaksim/statistic). The bot allows users to **track egg production**, view **daily, weekly, and monthly statistics**, and generate **graphs** based on collected data.

## Features
- Add daily egg counts via Telegram.
- Retrieve statistics for various time periods (day, week, month, year).
- Generate **charts** using XChart and send them as images.
- Integrates with a **JavaFX-based application** for advanced data analysis.

## Prerequisites
- **Java 17+**
- **Maven**
- **Telegram Bot API token**
- **Spring Boot** framework

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/mazurmaksim/telegram_bot_eggs.git
   cd telegram_bot_eggs
   ```
2. Set up your **Telegram Bot Token** in `application.properties`:
   ```properties
   bot.token=YOUR_TELEGRAM_BOT_TOKEN
   bot.name=YourBotName
   ```
3. Build and run the project:
   ```sh
   mvn clean install
   java -jar target/telegram_bot_eggs.jar
   ```

## Usage
1. Start the bot in Telegram by sending the `/start` command.
2. Use the provided buttons to get **egg production statistics**.
3. The bot will generate and send **line charts** visualizing your data.

## Technologies Used
- **Spring Boot** – For backend logic
- **Telegram Bots API** – For interacting with users
- **XChart** – For generating visual statistics
- **Maven** – For dependency management

## Related Project
This bot is an **extension** of the [Egg Statistics JavaFX application](https://github.com/mazurmaksim/statistic). The JavaFX app provides a **graphical interface** and advanced analytics for egg production tracking.

## License
This project is licensed under the MIT License.

---

**Author:** [mazurmaksim](https://github.com/mazurmaksim)

