package be.loisirs.tfe2025.plateforme_loisirs.util;


public final class EnterpriseNumber {

    public static final String MESSAGE =
            "Le numéro d'entreprise doit contenir 10 chiffres, commencer par 0 ou 1 "
                    + "et respecter la clé de contrôle modulo 97.";

    private static final String FORMAT = "^[01]\\d{9}$";

    private EnterpriseNumber() {
        // Classe utilitaire : pas d'instance.
    }

    /**
     * Retire les séparateurs de saisie courants (points, espaces, tirets).
     * "0123.456.749" -> "0123456749". Retourne null si l'entrée est null.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        return raw.replaceAll("[\\s.\\-]", "");
    }

    /** Vérifie un numéro déjà normalisé : format puis clé modulo 97. */
    public static boolean isValid(String normalized) {
        if (normalized == null || !normalized.matches(FORMAT)) {
            return false;
        }

        long base = Long.parseLong(normalized.substring(0, 8));
        int key = Integer.parseInt(normalized.substring(8));

        // Reste nul : la clé vaut 97, pas 0
        long remainder = base % 97;
        long expected = (remainder == 0) ? 97 : 97 - remainder;

        return key == expected;
    }

    // Numéro de TVA belge déduit du numéro d'entreprise : jamais saisi à la main. */
    public static String toVatNumber(String normalized) {
        return "BE" + normalized;
    }
}