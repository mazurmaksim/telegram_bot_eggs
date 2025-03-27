package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.repository.CounterRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CounterService {
    private final CounterRepository counterRepository;

    public CounterService(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Transactional
    public void saveCounter(Counter counter) {
        if (counter != null) {
            counterRepository.save(counter);
        }
    }

    public Optional<Counter> getCounterByDate(LocalDate date) {
        return counterRepository.findCounterByDate(date);
    }

    public List<Counter> getAllStatistic() {
        return counterRepository.findAllCounters();
    }

    public Map<Integer, Integer> calculateAmountByWeek(List<Counter> previous) {
        Map<Integer, Integer> amountByMonth = new HashMap<>();
        for (Counter counter : previous) {
            if(counter.getDateTime().getMonth().equals(LocalDate.now().getMonth()) && counter.getDateTime().getYear() == LocalDate.now().getYear()) {
                amountByMonth.put(counter.getDateTime().getDayOfMonth(), amountByMonth.getOrDefault(counter.getDateTime().getDayOfMonth(), 0) + counter.getAmount());
            }
        }
        return amountByMonth;
    }

    public Map<Month, Integer> calculateAmountByMonth(List<Counter> previous) {
        Map<Month, Integer> amountByMonth = new HashMap<>();
        for (Counter counter : previous) {
            if (counter.getDateTime().getYear() == LocalDate.now().getYear())
                amountByMonth.put(counter.getDateTime().getMonth(), amountByMonth.getOrDefault(counter.getDateTime().getMonth(), 0) + counter.getAmount());
        }
        return amountByMonth;
    }

}
