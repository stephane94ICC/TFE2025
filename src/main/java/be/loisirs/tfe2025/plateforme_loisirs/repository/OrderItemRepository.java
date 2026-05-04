package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
}
