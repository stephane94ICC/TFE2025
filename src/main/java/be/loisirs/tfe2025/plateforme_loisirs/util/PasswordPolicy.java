package be.loisirs.tfe2025.plateforme_loisirs.util;


public final class PasswordPolicy {

    /** 12 caractères minimum, avec au moins une minuscule, une majuscule, un chiffre et un caractère spécial. */
    public static final String PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{12,}$";

    public static final String MESSAGE =
            "Le mot de passe doit contenir au moins 12 caractères, avec une majuscule, "
                    + "une minuscule, un chiffre et un caractère spécial.";

    private PasswordPolicy() {
        // Classe utilitaire : pas d'instance.
    }
}