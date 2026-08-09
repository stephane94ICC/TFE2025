package be.loisirs.tfe2025.plateforme_loisirs.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {

    private List<CheckoutItemRequestDTO> items = new ArrayList<>();
}
