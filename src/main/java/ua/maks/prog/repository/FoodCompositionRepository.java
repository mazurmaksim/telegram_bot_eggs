package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.entity.FeedComposition;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FoodCompositionRepository extends JpaRepository<FeedComposition, UUID> {
    @Query("SELECT c.name FROM FeedComposition c WHERE c.active = true")
    String findActiveCompositionName();

    @Query(value = "SELECT * FROM feed_composition WHERE name = :compName", nativeQuery = true)
    FeedComposition findFeedCompositionByName(@Param("compName")String compName);
}
