package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.maks.prog.entity.Sales;

import java.time.LocalDate;
import java.util.UUID;

public interface SalesRepository extends JpaRepository<Sales, UUID> {
    @Query("SELECT s FROM Sales s WHERE s.dateToThisAmount = :date")
    Sales findAmountToSale(@Param("date") LocalDate date);
}
