package be.loisirs.tfe2025.plateforme_loisirs.dto;

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
public class OrderDTO {

    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Long userId;
    private String userEmail;
    private List<OrderItemDTO> items;
}
