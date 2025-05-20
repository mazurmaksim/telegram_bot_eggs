package ua.maks.prog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.BotAdmin;
import ua.maks.prog.repository.BotAdminRepository;

import java.util.List;

@Component
public class BotAdminService {

    private static final Logger logger = LoggerFactory.getLogger(BotAdminService.class);
    private final BotAdminRepository botAdminRepository;

    public BotAdminService(BotAdminRepository botAdminRepository) {
        this.botAdminRepository = botAdminRepository;
    }

    public List<BotAdmin> getBotAdmins() {
        List<BotAdmin> admins = botAdminRepository.getAdmins();
        logger.debug("Retrieved {} bot admins", admins.size());
        return admins;
    }
}
