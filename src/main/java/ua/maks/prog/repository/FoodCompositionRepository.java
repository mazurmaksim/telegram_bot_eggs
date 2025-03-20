package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.maks.prog.entity.FeedComposition;

import java.util.UUID;

public interface FoodCompositionRepository extends JpaRepository<FeedComposition, UUID> {
    @Query("SELECT c.name FROM FeedComposition c WHERE c.active = true")
    String findActiveCompositionName();

    @Query("SELECT c FROM FeedComposition c WHERE c.name =: compName")
    FeedComposition findFeedCompositionByName(@Param("compName")String compName);
}
