package ua.maks.prog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "amount_to_sale")
public class Sales {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private Integer amountToSale;
    @Column(name = "date_to_this_amount")
    private LocalDate dateToThisAmount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAmountToSale() {
        return amountToSale;
    }

    public void setAmountToSale(Integer amountToSale) {
        this.amountToSale = amountToSale;
    }

    public LocalDate getDateToThisAmount() {
        return dateToThisAmount;
    }

    public void setDateToThisAmount(LocalDate dateToThisAmount) {
        this.dateToThisAmount = dateToThisAmount;
    }
}
