package ua.maks.prog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.maks.prog.entity.FeedComposition;
import ua.maks.prog.repository.FoodCompositionRepository;

@Service
public class FeedCompositionService {
    private FoodCompositionRepository foodCompositionRepository;

    public FeedCompositionService(FoodCompositionRepository foodCompositionRepository) {
        this.foodCompositionRepository = foodCompositionRepository;
    }

    @Transactional
    public String findActiveCompositionName() {
        return foodCompositionRepository.findActiveCompositionName();
    }

    @Transactional
    public FeedComposition findFeedCompositionByName(String name) {
        return foodCompositionRepository.findFeedCompositionByName(name);
    }
}
