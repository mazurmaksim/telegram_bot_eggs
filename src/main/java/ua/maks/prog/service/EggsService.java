package ua.maks.prog.service;

import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.FeedComposition;
import ua.maks.prog.entity.WeatherForecast;
import ua.maks.prog.weather.service.WeatherService;
import ua.maks.prog.weather.service.forecast.WeatherParser;
import ua.maks.prog.weather.service.forecast.WeatherResponse;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class EggsService {
    private final FeedCompositionService feedCompositionService;
    private final CounterService counterService;
    private final WeatherService weatherService;

    public EggsService(FeedCompositionService feedCompositionService, CounterService counterService, WeatherService weatherService) {
        this.feedCompositionService = feedCompositionService;
        this.counterService = counterService;
        this.weatherService = weatherService;
    }

    public void addEgg(String amountInput) throws NumberFormatException{
//            TODO: amount from telegram
        int amount = Integer.parseInt(amountInput);
        LocalDate date = LocalDate.now();

        Counter entry = new Counter();
        Optional<Counter> counter = counterService.getCounterByDate(date);

        if (counter.isEmpty()) {
            WeatherResponse weatherResponse = getWeatherForecast();
            WeatherForecast weatherForecast = new WeatherForecast();

            if (weatherResponse != null) {
                weatherForecast.setHumidity(weatherResponse.getMain().getHumidity());
                weatherForecast.setTemperature(weatherResponse.getMain().getTemp());
                weatherForecast.setWindSpeed(weatherResponse.getWind().getSpeed());
                weatherForecast.setRetrievedSuccessfully(true);
            }

            String foodCompositionName = feedCompositionService.findActiveCompositionName();
            FeedComposition feedComposition = feedCompositionService.findFeedCompositionByName(foodCompositionName);

            entry.setAmount(amount);
            entry.setDateTime(date);
            entry.setWeatherForecast(weatherForecast);
            entry.setFeedComposition(feedComposition);
            weatherForecast.setDayStatistic(entry);
            counterService.saveCounter(entry);
        } else {
            Integer updatedAmount = counter.get().getAmount() + amount;
            counter.get().setAmount(updatedAmount);
            counterService.saveCounter(counter.get());
//                TODO: Send message to telegram "Entry for this date already exists pleas use command Update"
            System.out.println("You can not add eggs you can update");
        }
    }



    private WeatherResponse getWeatherForecast() {
        try {
            WeatherParser parser = new WeatherParser();
            return parser.parseWeatherJson(weatherService.getWeather());
        } catch (Exception ex) {

        }
        return null;
    }
}
