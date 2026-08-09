package be.loisirs.tfe2025.plateforme_loisirs.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {

    private Long orderId;
    private String stripeSessionId;
    private String checkoutUrl;
}
