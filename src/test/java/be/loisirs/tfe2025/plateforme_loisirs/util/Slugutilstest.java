package be.loisirs.tfe2025.plateforme_loisirs.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires purs : aucun contexte Spring, aucune base.
 * Le slug devient une URL publique : son format est un engagement.
 */
@DisplayName("SlugUtils — transformation d'un nom en segment d'URL")
class SlugUtilsTest {

    @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
    @DisplayName("Accents, ligatures, majuscules et caractères spéciaux")
    @CsvSource(delimiter = '|', value = {
            "Cuisine & Partage          | cuisine-partage",
            "Équitation au Château      | equitation-au-chateau",
            "Cœur de Bœuf               | coeur-de-boeuf",
            "Straße                     | strasse",
            "'  --Danse   Avenue--  '   | danse-avenue",
            "Bruxelles Yoga Studio      | bruxelles-yoga-studio"
    })
    void slugifiesReadableNames(String name, String expected) {
        assertThat(SlugUtils.slugify(name)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{0}] → \"partenaire\"")
    @DisplayName("Nom vide, nul ou sans caractère utilisable : valeur de repli")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "@@@", "---"})
    void fallsBackWhenNothingUsable(String name) {
        assertThat(SlugUtils.slugify(name)).isEqualTo("partenaire");
    }

    @Test
    @DisplayName("Nom trop long : tronqué à 80 caractères")
    void truncatesTo80Characters() {
        assertThat(SlugUtils.slugify("a".repeat(100))).hasSize(80);
    }

    @Test
    @DisplayName("Troncature sur un séparateur : aucun tiret final")
    void truncationNeverEndsWithHyphen() {
        // Le 80e caractère tombe sur le tiret entre les deux mots
        String slug = SlugUtils.slugify("a".repeat(79) + " bcdef");

        assertThat(slug).isEqualTo("a".repeat(79));
        assertThat(slug).doesNotEndWith("-");
    }
}