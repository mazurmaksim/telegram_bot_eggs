package ua.maks.prog.weather.service.forecast;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherParser {
    public WeatherResponse parseWeatherJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, WeatherResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
