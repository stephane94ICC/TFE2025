package be.loisirs.tfe2025.plateforme_loisirs.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Transforme un nom en segment d'URL lisible : sans accents, en minuscules,
 * les caractères spéciaux remplacés par des tirets.
 * Exemple : "Cuisine & Partage" -> "cuisine-partage".
 */
public final class SlugUtils {

    private static final int MAX_LENGTH = 80;
    private static final String FALLBACK = "partenaire";

    private SlugUtils() {
    }

    public static String slugify(String text) {
        if (text == null || text.isBlank()) {
            return FALLBACK;
        }

        // Ligatures que la décomposition Unicode ne sépare pas
        String expanded = text
                .replace("œ", "oe").replace("Œ", "OE")
                .replace("æ", "ae").replace("Æ", "AE")
                .replace("ß", "ss");

        // "é" devient "e" + accent, puis l'accent est supprimé
        String withoutAccents = Normalizer.normalize(expanded, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        if (slug.length() > MAX_LENGTH) {
            slug = slug.substring(0, MAX_LENGTH).replaceAll("-+$", "");
        }

        return slug.isEmpty() ? FALLBACK : slug;
    }
}