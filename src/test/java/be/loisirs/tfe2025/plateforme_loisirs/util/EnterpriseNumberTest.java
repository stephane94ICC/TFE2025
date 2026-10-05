package be.loisirs.tfe2025.plateforme_loisirs.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitaire du numéro d'entreprise, sans Spring ni base de données.
 */
@DisplayName("Numéro d'entreprise belge (modulo 97)")
class EnterpriseNumberTest {

    @Test
    @DisplayName("Saisie avec points et espaces : normalisée puis acceptée")
    void normalizesAndAcceptsValidNumber() {
        String normalized = EnterpriseNumber.normalize(" 0123.456.749 ");

        assertThat(normalized).isEqualTo("0123456749");
        assertThat(EnterpriseNumber.isValid(normalized)).isTrue();
    }

    @Test
    @DisplayName("Clé de contrôle fausse : refusé")
    void rejectsWrongCheckDigits() {
        assertThat(EnterpriseNumber.isValid("0123456748")).isFalse();
    }

    @Test
    @DisplayName("Reste nul : la clé attendue est 97")
    void acceptsKey97WhenRemainderIsZero() {
        assertThat(EnterpriseNumber.isValid("0000009797")).isTrue();
    }

    @Test
    @DisplayName("Premier chiffre autre que 0 ou 1 : refusé même avec une clé correcte")
    void rejectsWrongFirstDigit() {
        // 21234567 mod 97 = 6 -> clé 91 : la clé est juste, le format non
        assertThat(EnterpriseNumber.isValid("2123456791")).isFalse();
    }

    @Test
    @DisplayName("Longueur, lettres ou null : refusé")
    void rejectsMalformedInput() {
        assertThat(EnterpriseNumber.isValid("012345674")).isFalse();
        assertThat(EnterpriseNumber.isValid("01234567490")).isFalse();
        assertThat(EnterpriseNumber.isValid("BE0123456749")).isFalse();
        assertThat(EnterpriseNumber.isValid(null)).isFalse();
        assertThat(EnterpriseNumber.normalize(null)).isNull();
    }

    @Test
    @DisplayName("TVA déduite : BE + numéro d'entreprise")
    void derivesVatNumber() {
        assertThat(EnterpriseNumber.toVatNumber("0123456749")).isEqualTo("BE0123456749");
    }
}