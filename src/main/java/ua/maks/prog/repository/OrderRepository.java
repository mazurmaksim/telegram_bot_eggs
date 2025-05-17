package ua.maks.prog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.maks.prog.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> getOrderByPhoneNumber(String phoneNumber);
}
