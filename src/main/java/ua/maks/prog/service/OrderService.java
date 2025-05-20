package ua.maks.prog.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.maks.prog.entity.Order;
import ua.maks.prog.repository.OrderRepository;
import ua.maks.prog.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

@Component
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void saveOrder(Order order) {
        if (order != null) {
            orderRepository.save(order);
            LOGGER.info("✅ Saved order: id={}, chatId={}, phone={}", order.getId(), order.getChatId(), order.getPhoneNumber());
        } else {
            LOGGER.warn("⚠️ Tried to save null order");
        }
    }

    public List<Order> getOrderByChatId(Long chatId) {
        if (chatId != null) {
            List<Order> orders = orderRepository.getOrderByChatId(chatId);
            LOGGER.debug("📦 Retrieved {} orders for chatId={}", orders.size(), chatId);
            return orders;
        } else {
            LOGGER.warn("⚠️ getOrderByChatId called with null chatId");
            return null;
        }
    }

    public List<Order> getOrderByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.getOrdersByStatus(status);
        LOGGER.debug("📦 Retrieved {} orders with status={}", orders.size(), status);
        return orders;
    }

    public List<Order> getOrderById(UUID orderId) {
        List<Order> orders = orderRepository.getOrderById(orderId);
        LOGGER.debug("📦 Retrieved {} orders with id={}", orders.size(), orderId);
        return orders;
    }

    public List<Order> getOrderByPhoneNumber(String phone) {
        List<Order> orders = orderRepository.getOrderByPhoneNumber(phone);
        LOGGER.debug("📞 Retrieved {} orders for phone={}", orders.size(), phone);
        return orders;
    }
}
