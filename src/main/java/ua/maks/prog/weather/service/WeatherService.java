package ua.maks.prog.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.maks.prog.entity.Settings;
import ua.maks.prog.service.SettingsService;

import java.util.List;

@Service
public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static String apiKey;
    private static String city;

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
                return restTemplate.getForObject(url, String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
