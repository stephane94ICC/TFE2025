package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ne teste aucune regle metier : valide la plomberie d'integration.
 * Si cette classe echoue, aucun scenario ne pourra tourner.
 */
@DisplayName("Socle d'intégration")
class IntegrationSmokeTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Un membre créé en test peut se connecter et recevoir un jeton")
    void createdMemberCanLogIn() throws Exception {
        User member = createMember("smoke@belloisirs.test");
        assertThat(member.getId()).isNotNull();

        String token = tokenFor(member.getEmail());

        // Un JWT compte trois segments separes par un point.
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

}