package ua.maks.prog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "weather_forecast", schema = "public")
public class WeatherForecast {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "day_statistic_id", nullable = false)
    private Counter dayStatistic;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Integer humidity;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "retrieved_successfully")
    private Boolean retrievedSuccessfully;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Counter getDayStatistic() {
        return dayStatistic;
    }

    public void setDayStatistic(Counter dayStatistic) {
        this.dayStatistic = dayStatistic;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Boolean getRetrievedSuccessfully() {
        return retrievedSuccessfully;
    }

    public void setRetrievedSuccessfully(Boolean retrievedSuccessfully) {
        this.retrievedSuccessfully = retrievedSuccessfully;
    }
}