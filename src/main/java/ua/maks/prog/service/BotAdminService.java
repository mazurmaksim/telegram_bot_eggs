package ua.maks.prog.service;

import org.springframework.stereotype.Component;
import ua.maks.prog.entity.BotAdmin;
import ua.maks.prog.repository.BotAdminRepository;

import java.util.List;

@Component
public class BotAdminService {
    private final BotAdminRepository botAdminRepository;

    public BotAdminService(BotAdminRepository botAdminRepository) {
        this.botAdminRepository = botAdminRepository;
    }

    public List<BotAdmin> getBotAdmins() {
        return botAdminRepository.getAdmins();
    }
}
