package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.OrderDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.OrderItemDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> items = order.getOrderItems()
                .stream()
                .map(OrderMapper::toItemDTO)
                .toList();

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                items
        );
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                subtotal
        );
    }
}
