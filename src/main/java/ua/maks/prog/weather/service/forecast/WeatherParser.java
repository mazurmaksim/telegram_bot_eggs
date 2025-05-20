package ua.maks.prog.weather.service.forecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherParser.class);

    public WeatherResponse parseWeatherJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, WeatherResponse.class);
        } catch (Exception e) {
            LOGGER.error("🌩️ Failed to parse weather JSON: {}", e.getMessage(), e);
            return null;
        }
    }
}
