package be.loisirs.tfe2025.plateforme_loisirs.dto;

import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberOrderResponseDTO {

    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime paidAt;
    private List<OrderItemDTO> items;
}