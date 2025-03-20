package ua.maks.prog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.maks.prog.entity.Counter;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CounterRepository extends JpaRepository<Counter, UUID> {
    @Query("SELECT c FROM Counter c WHERE c.dateTime =: countDate")
    Optional<Counter> findCounterByDate(LocalDate countDate);
}
