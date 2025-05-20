package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Sales;
import ua.maks.prog.repository.SalesRepository;

import java.time.LocalDate;

@Component
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);
    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Transactional
    public void saveAmountToSale(Sales sales) {
        if (sales != null) {
            salesRepository.save(sales);
            logger.info("💰 Saved sales record: date={}, amount={}", sales.getDateToThisAmount(), sales.getAmountToSale());
        } else {
            logger.warn("⚠️ Tried to save null sales object");
        }
    }

    public Sales getAmoutToSale(LocalDate date) {
        Sales sales = salesRepository.findAmountToSale(date);
        if (sales != null) {
            logger.debug("📊 Found sales record for date {}: amount={}", date, sales.getAmountToSale());
        } else {
            logger.debug("📊 No sales record found for date {}", date);
        }
        return sales;
    }
}
