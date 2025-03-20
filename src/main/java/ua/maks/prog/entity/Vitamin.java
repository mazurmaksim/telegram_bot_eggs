package ua.maks.prog.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "vitamin")
@Access(AccessType.PROPERTY)
public class Vitamin {

    private UUID id;
    private FeedComposition feedComposition;
    private String vitaminName;
    private String quantity;
    public Vitamin() {}

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "feed_composition_id", nullable = false)
    public FeedComposition getFeedComposition() {
        return feedComposition;
    }

    public void setFeedComposition(FeedComposition feedComposition) {
        this.feedComposition = feedComposition;
    }

    @Column(nullable = false, name = "name")
    public String getVitaminName() {
        return vitaminName;
    }

    public void setVitaminName(String vitaminName) {
        this.vitaminName = vitaminName;
    }

    @Column(nullable = false)
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}