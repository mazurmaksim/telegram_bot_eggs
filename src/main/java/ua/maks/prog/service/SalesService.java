package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Sales;
import ua.maks.prog.repository.SalesRepository;

import java.time.LocalDate;

@Component
public class SalesService {
    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Transactional
    public void saveAmountToSale(Sales sales) {
        if(sales !=null) {
            salesRepository.save(sales);
        }
    }

    public Sales getAmoutToSale(LocalDate date) {
        return salesRepository.findAmountToSale(date);
    }
}
