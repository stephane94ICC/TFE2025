package be.loisirs.tfe2025.plateforme_loisirs.api.controller.member;

import be.loisirs.tfe2025.plateforme_loisirs.dto.MemberOrderResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/member/orders")
public class MemberOrderApiController {

    private final OrderService orderService;

    public MemberOrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
     * Aucun identifiant dans l'URL : l'utilisateur est celui du jeton JWT.
     * Un membre ne peut donc lire que ses propres commandes (pas d'IDOR).
     */
    @GetMapping
    public ResponseEntity<List<MemberOrderResponseDTO>> getOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrdersForMember(principal.getName()));
    }
}