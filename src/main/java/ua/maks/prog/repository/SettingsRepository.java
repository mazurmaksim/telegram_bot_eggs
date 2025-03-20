package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.maks.prog.entity.Settings;

import java.util.List;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<Settings, UUID> {

    @Query("SELECT c FROM Settings c")
    List<Settings> findAllSettings();
}
