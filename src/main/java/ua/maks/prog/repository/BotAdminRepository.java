package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.maks.prog.entity.BotAdmin;
import ua.maks.prog.entity.Sales;

import java.util.List;
import java.util.UUID;

public interface BotAdminRepository extends JpaRepository<BotAdmin, UUID> {

    @Query("SELECT s FROM BotAdmin s")
    List<BotAdmin> getAdmins();
}
