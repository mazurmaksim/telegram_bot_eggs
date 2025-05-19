package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.maks.prog.entity.Order;
import ua.maks.prog.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> getOrderByChatId(Long phoneNumber);
    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getOrderById(UUID orderId);

    List<Order> getOrderByPhoneNumber(String phone);
}
