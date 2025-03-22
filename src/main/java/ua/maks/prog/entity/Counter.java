package ua.maks.prog.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "day_statistic")
@Access(AccessType.PROPERTY)
public class
Counter implements Comparable<Counter> {

    private UUID id;
    private LocalDate dateTime;
    private Integer amount;
    private FeedComposition feedComposition;
    private WeatherForecast weatherForecast;
    private Chickens chickens;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    public UUID getId() {
        return id;
    }

    @OneToOne(mappedBy = "dayStatistic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public WeatherForecast getWeatherForecast() {
        return weatherForecast;
    }

    @OneToOne(mappedBy = "dayStatistic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Chickens getChickens() {
        return chickens;
    }

    public void setChickens(Chickens chickens) {
        this.chickens = chickens;
    }

    public void setWeatherForecast(WeatherForecast weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    @OneToOne(mappedBy = "dayStatistic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)


    public void setId(UUID id) {
        this.id = id;
    }

    @Column(name = "saved_at")
    public LocalDate getDateTime() {
        return dateTime;
    }

    @ManyToOne
    @JoinColumn(name = "feed_composition_id", nullable = false)
    public FeedComposition getFeedComposition() {
        return feedComposition;
    }

    public void setFeedComposition(FeedComposition feedComposition) {
        this.feedComposition = feedComposition;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    @Column(name = "amount")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return Objects.equals(id, counter.id) && Objects.equals(dateTime, counter.dateTime) && Objects.equals(amount, counter.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTime, amount);
    }

    @Override
    public int compareTo(Counter other) {
        return this.dateTime.compareTo(other.dateTime);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", amount=" + amount +
                ", feedComposition=" + feedComposition +
                ", weatherForecast=" + weatherForecast +
                '}';
    }
}
