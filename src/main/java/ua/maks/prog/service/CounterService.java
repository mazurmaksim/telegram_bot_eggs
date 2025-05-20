package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.repository.CounterRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class CounterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterService.class);

    private final CounterRepository counterRepository;

    public CounterService(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Transactional
    public void saveCounter(Counter counter) {
        if (counter != null) {
            counterRepository.save(counter);
            LOGGER.info("Counter saved: date={}, amount={}", counter.getDateTime(), counter.getAmount());
        } else {
            LOGGER.warn("Tried to save null counter");
        }
    }

    public Optional<Counter> getCounterByDate(LocalDate date) {
        Optional<Counter> counter = counterRepository.findCounterByDate(date);
        if (counter.isPresent()) {
            LOGGER.debug("Found counter for date {}: amount={}", date, counter.get().getAmount());
        } else {
            LOGGER.debug("No counter found for date {}", date);
        }
        return counter;
    }

    public List<Counter> getAllStatistic() {
        List<Counter> stats = counterRepository.findAllCounters();
        LOGGER.debug("Retrieved full statistics, {} entries", stats.size());
        return stats;
    }

    public Map<Integer, Integer> calculateAmountByWeek(List<Counter> previous) {
        Map<Integer, Integer> amountByDay = new HashMap<>();
        for (Counter counter : previous) {
            if (counter.getDateTime().getMonth().equals(LocalDate.now().getMonth())
                    && counter.getDateTime().getYear() == LocalDate.now().getYear()) {
                int day = counter.getDateTime().getDayOfMonth();
                amountByDay.put(day, amountByDay.getOrDefault(day, 0) + counter.getAmount());
            }
        }
        LOGGER.debug("Weekly chart data calculated: {} days", amountByDay.size());
        return amountByDay;
    }

    public Map<Month, Integer> calculateAmountByMonth(List<Counter> previous) {
        Map<Month, Integer> amountByMonth = new HashMap<>();
        for (Counter counter : previous) {
            if (counter.getDateTime().getYear() == LocalDate.now().getYear()) {
                Month month = counter.getDateTime().getMonth();
                amountByMonth.put(month, amountByMonth.getOrDefault(month, 0) + counter.getAmount());
            }
        }
        LOGGER.debug("Monthly chart data calculated: {} months", amountByMonth.size());
        return new TreeMap<>(amountByMonth);
    }
}
