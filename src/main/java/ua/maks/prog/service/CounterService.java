package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.repository.CounterRepository;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CounterService {
    private CounterRepository counterRepository;

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


}
