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
@Table(name = "chickens_amount")
public class Chickens {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name="chicken_amount")
    private String chickenAmount;

    @OneToOne
    @JoinColumn(name = "day_statistic_id", nullable = false)
    private Counter dayStatistic;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChickenAmount() {
        return chickenAmount;
    }

    public void setChickenAmount(String chickenAmount) {
        this.chickenAmount = chickenAmount;
    }

    public Counter getDayStatistic() {
        return dayStatistic;
    }

    public void setDayStatistic(Counter dayStatistic) {
        this.dayStatistic = dayStatistic;
    }
}
