package ua.maks.prog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.maks.prog.entity.Settings;
import ua.maks.prog.repository.SettingsRepository;

import java.util.List;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Transactional
    public List<Settings> getAllSettings() {
        return settingsRepository.findAllSettings();
    }
}
