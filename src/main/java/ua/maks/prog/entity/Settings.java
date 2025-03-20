package ua.maks.prog.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name="app_settings")
public class Settings {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private WeatherSettings weatherSettings;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private ChickensSettings chickenSettings;

    public ChickensSettings getChickenSettingsSettings() {
        return chickenSettings;
    }

    public void setChickenSettings(ChickensSettings chickenSettings) {
        this.chickenSettings = chickenSettings;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WeatherSettings getWeatherSettings() {
        return weatherSettings;
    }

    public void setWeatherSettings(WeatherSettings weatherSettings) {
        this.weatherSettings = weatherSettings;
    }
}
