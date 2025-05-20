package ua.maks.prog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.FeedComposition;
import ua.maks.prog.entity.WeatherForecast;
import ua.maks.prog.weather.service.WeatherService;
import ua.maks.prog.weather.service.forecast.WeatherParser;
import ua.maks.prog.weather.service.forecast.WeatherResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EggsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EggsService.class);

    private final FeedCompositionService feedCompositionService;
    private final CounterService counterService;
    private final WeatherService weatherService;

    public EggsService(FeedCompositionService feedCompositionService, CounterService counterService, WeatherService weatherService) {
        this.feedCompositionService = feedCompositionService;
        this.counterService = counterService;
        this.weatherService = weatherService;
    }

    public void addEgg(String amountInput, LocalDateTime savingTime) {
        try {
            int amount = Integer.parseInt(amountInput);
            LocalDate date = savingTime.toLocalDate();

            Optional<Counter> counter = counterService.getCounterByDate(date);

            if (counter.isEmpty()) {
                WeatherResponse weatherResponse = getWeatherForecast();
                WeatherForecast weatherForecast = new WeatherForecast();

                if (weatherResponse != null) {
                    weatherForecast.setHumidity(weatherResponse.getMain().getHumidity());
                    weatherForecast.setTemperature(weatherResponse.getMain().getTemp());
                    weatherForecast.setWindSpeed(weatherResponse.getWind().getSpeed());
                    weatherForecast.setRetrievedSuccessfully(true);
                    LOGGER.debug("Weather fetched for {}: {}°C, {}% humidity, {} m/s wind",
                            date, weatherForecast.getTemperature(), weatherForecast.getHumidity(), weatherForecast.getWindSpeed());
                } else {
                    LOGGER.warn("Weather data not available for {}", date);
                }

                String foodCompositionName = feedCompositionService.findActiveCompositionName();
                FeedComposition feedComposition = feedCompositionService.findFeedCompositionByName(foodCompositionName);

                Counter entry = new Counter();
                entry.setAmount(amount);
                entry.setDateTime(date);
                entry.setWeatherForecast(weatherForecast);
                entry.setFeedComposition(feedComposition);
                weatherForecast.setDayStatistic(entry);

                counterService.saveCounter(entry);
                LOGGER.info("New egg entry saved: date={}, amount={}", date, amount);
            } else {
                Integer updatedAmount = counter.get().getAmount() + amount;
                counter.get().setAmount(updatedAmount);
                counterService.saveCounter(counter.get());
                LOGGER.warn("Entry for {} already existed — amount updated to {}", date, updatedAmount);
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid input for egg amount: '{}'", amountInput);
        } catch (Exception e) {
            LOGGER.error("Unexpected error in addEgg(): {}", e.getMessage(), e);
        }
    }

    private WeatherResponse getWeatherForecast() {
        try {
            WeatherParser parser = new WeatherParser();
            return parser.parseWeatherJson(weatherService.getWeather());
        } catch (Exception ex) {
            LOGGER.error("Failed to parse weather JSON: {}", ex.getMessage(), ex);
            return null;
        }
    }
}
