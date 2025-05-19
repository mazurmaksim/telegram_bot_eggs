package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Order;
import ua.maks.prog.repository.OrderRepository;
import ua.maks.prog.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

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

    public List<Order> getOrderByStatus(OrderStatus status) {
        return orderRepository.getOrdersByStatus(status);
    }

    public List<Order> getOrderById(UUID orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public List<Order> getOrderByPhoneNumber(String phone) {
        return orderRepository.getOrderByPhoneNumber(phone);
    }
}
