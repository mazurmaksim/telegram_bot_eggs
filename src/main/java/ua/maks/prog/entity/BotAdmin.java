package ua.maks.prog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "bot_admin")
public class BotAdmin {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private Long botUserId;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBotUserId(Long botUserId) {
        this.botUserId = botUserId;
    }

    public Long getBotUserId() {
        return botUserId;
    }
}
