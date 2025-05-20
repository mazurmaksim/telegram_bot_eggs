package ua.maks.prog.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.maks.prog.entity.Settings;
import ua.maks.prog.service.SettingsService;

import java.util.List;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    private final SettingsService settingsService;
    private final RestTemplate restTemplate;

    public WeatherService(SettingsService settingsService, RestTemplate restTemplate) {
        this.settingsService = settingsService;
        this.restTemplate = restTemplate;
    }

    public String getWeather() {
        try {
            List<Settings> settings = settingsService.getAllSettings();
            String apiKey = null;
            String city = null;

            for (Settings s : settings) {
                if (s.getWeatherSettings() != null) {
                    apiKey = s.getWeatherSettings().getApiKey();
                    city = s.getWeatherSettings().getCity();
                }
            }

            if (apiKey != null && city != null) {
                String url = String.format(BASE_URL, city, apiKey);
                LOGGER.debug("🌍 Requesting weather for city: {}", city);
                return restTemplate.getForObject(url, String.class);
            } else {
                LOGGER.warn("⚠️ Weather settings are missing (apiKey or city is null)");
            }
        } catch (Exception e) {
            LOGGER.error("❌ Error while fetching weather: {}", e.getMessage(), e);
        }
        return null;
    }
}
