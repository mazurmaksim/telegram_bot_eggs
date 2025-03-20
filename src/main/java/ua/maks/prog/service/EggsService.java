package ua.maks.prog.service;

import ua.maks.prog.entity.Counter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EggsService {
    private void onManualSaveButtonClick() {
        try {
            List<Counter> allData = new ArrayList<>(tableView.getItems());

//            TODO: amount from telegram
            int amount = Integer.parseInt("10");
            LocalDate date = datePicker.getValue();
            String foodCompositionName = foodPlanChoice.getValue();
            Counter entry = new Counter();

            WeatherResponse weatherResponse = getWeatherForecast();
            WeatherForecast weatherForecast = new WeatherForecast();

            if(weatherResponse !=null) {
                weatherForecast.setHumidity(weatherResponse.getMain().getHumidity());
                weatherForecast.setTemperature(weatherResponse.getMain().getTemp());
                weatherForecast.setWindSpeed(weatherResponse.getWind().getSpeed());
                weatherForecast.setRetrievedSuccessfully(true);
            }

            if (date == null) {
                showError("Please select a date.");
                return;
            }
            FeedComposition feedComposition = StatisticDao.getFeedCompositionByName(foodCompositionName);
            for (Counter counter : allData) {
                if(counter.getWeatherForecast() == null) {
                    entry.setWeatherForecast(weatherForecast);
                }
                if (date.equals(counter.getDateTime())) {
                    showError("You trying  insert already existing record for date: " + date
                            + System.lineSeparator() +
                            "If You want update use update button");
                    return;
                }
            }

            if (date.isAfter(LocalDate.now())) {
                showError("Impossible to add value to a past day: " + date);
                return;
            }

            entry.setAmount(amount);
            entry.setDateTime(date);
            entry.setFeedComposition(feedComposition);
            Persistence<Counter> saver = new Persistence<>();
            weatherForecast.setDayStatistic(entry);

            saver.persist(entry);

            addManually.clear();
            datePicker.setValue(null);
            initialize();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number of eggs.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

}
