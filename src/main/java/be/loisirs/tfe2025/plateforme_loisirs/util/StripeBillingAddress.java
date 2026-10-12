package be.loisirs.tfe2025.plateforme_loisirs.util;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import com.stripe.model.Address;
import com.stripe.model.checkout.Session;

import java.util.Locale;

/**
 * Recopie sur la réservation l'adresse de facturation saisie dans Stripe Checkout
 * (customer_details.address), au moment où le webhook confirme le paiement.
 * Le webhook confirme un paiement déjà encaissé : il ne doit jamais échouer à cause
 * de l'adresse. Une adresse absente laisse les colonnes vides (justificatif seulement),
 */
public final class StripeBillingAddress {

    private StripeBillingAddress() {
    }

    public static void copyTo(Reservation reservation, Session.CustomerDetails details) {
        Address address = details == null ? null : details.getAddress();

        if (address == null) {
            return;
        }

        reservation.setBillingAddressLine1(limit(address.getLine1(), 255));
        reservation.setBillingAddressLine2(limit(address.getLine2(), 255));
        reservation.setBillingPostalCode(limit(address.getPostalCode(), 20));
        reservation.setBillingCity(limit(address.getCity(), 100));
        reservation.setBillingCountry(country(address.getCountry()));
    }

    /** Espaces retirés ; une valeur vide devient null ; coupée à la longueur de la colonne. */
    static String limit(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.strip();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    /** Code pays ISO à 2 lettres (BE, FR, NL...) ; toute autre forme est ignorée. */
    static String country(String code) {
        if (code == null || !code.strip().matches("[A-Za-z]{2}")) {
            return null;
        }

        return code.strip().toUpperCase(Locale.ROOT);
    }
}