package ua.maks.prog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.maks.prog.entity.Counter;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CounterRepository extends JpaRepository<Counter, UUID> {

    @Query(value = "SELECT * FROM day_statistic WHERE saved_at = :dateTime", nativeQuery = true)
    Optional<Counter> findCounterByDate(@Param("dateTime") LocalDate dateTime);

}
