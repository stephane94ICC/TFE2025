package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.OrderDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.OrderMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + id));

        return OrderMapper.toDTO(order);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderMapper::toDTO)
                .toList();
    }
}
