package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Order;
import ua.maks.prog.repository.OrderRepository;

import java.util.List;

@Component
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void saveOrder(Order order) {
        if (order != null) {
            orderRepository.save(order);
        }
    }

    public List<Order> getOrderByChatId(Long chatId) {
        if (chatId != null) {
           return orderRepository.getOrderByChatId(chatId);
        } else {
            return null;
        }
    }
}
