package be.loisirs.tfe2025.plateforme_loisirs.util;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import com.stripe.model.Address;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires purs : aucun contexte Spring, aucune base, aucun appel à Stripe.
 * Les objets Stripe sont construits à la main, comme le webhook les reçoit.
 */
@DisplayName("StripeBillingAddress — adresse de facturation figée au paiement")
class StripeBillingAddressTest {

    @Test
    @DisplayName("Adresse belge complète : recopiée telle quelle")
    void copiesACompleteAddress() {
        Reservation reservation = new Reservation();

        StripeBillingAddress.copyTo(reservation, details(address(
                "Rue de la Loi 16", null, "1000", "Bruxelles", "BE")));

        assertThat(reservation.getBillingAddressLine1()).isEqualTo("Rue de la Loi 16");
        assertThat(reservation.getBillingAddressLine2()).isNull();
        assertThat(reservation.getBillingPostalCode()).isEqualTo("1000");
        assertThat(reservation.getBillingCity()).isEqualTo("Bruxelles");
        assertThat(reservation.getBillingCountry()).isEqualTo("BE");
    }

    @Test
    @DisplayName("Sans adresse (ancienne session, champ absent) : rien n'est écrit, aucune erreur")
    void ignoresMissingAddress() {
        Reservation reservation = new Reservation();

        StripeBillingAddress.copyTo(reservation, null);
        StripeBillingAddress.copyTo(reservation, new Session.CustomerDetails());

        assertThat(reservation.getBillingAddressLine1()).isNull();
        assertThat(reservation.getBillingCity()).isNull();
        assertThat(reservation.getBillingCountry()).isNull();
    }

    @Test
    @DisplayName("Valeurs nettoyées : espaces retirés, vide = null, trop long coupé, pays invalide ignoré")
    void cleansValues() {
        Reservation reservation = new Reservation();

        StripeBillingAddress.copyTo(reservation, details(address(
                "  Avenue Louise 54  ", "   ", "1050", "B".repeat(150), "BEL")));

        assertThat(reservation.getBillingAddressLine1()).isEqualTo("Avenue Louise 54");
        assertThat(reservation.getBillingAddressLine2()).isNull();
        assertThat(reservation.getBillingCity()).hasSize(100);
        assertThat(reservation.getBillingCountry()).isNull();
    }

    @Test
    @DisplayName("Code pays en minuscules : mis en majuscules")
    void uppercasesCountryCode() {
        assertThat(StripeBillingAddress.country("nl")).isEqualTo("NL");
    }

    private static Session.CustomerDetails details(Address address) {
        Session.CustomerDetails details = new Session.CustomerDetails();
        details.setAddress(address);
        return details;
    }

    private static Address address(String line1, String line2, String postalCode, String city, String country) {
        Address address = new Address();
        address.setLine1(line1);
        address.setLine2(line2);
        address.setPostalCode(postalCode);
        address.setCity(city);
        address.setCountry(country);
        return address;
    }
}