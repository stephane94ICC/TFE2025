package be.loisirs.tfe2025.plateforme_loisirs.entity;

public enum ActivityEventType {

    // --- Sécurité / authentification ---
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    REGISTER,
    ACCESS_DENIED_INACTIVE_ACCOUNT,

    // --- Données personnelles (RGPD) ---
    PROFILE_UPDATED,
    ACCOUNT_DELETION_REQUESTED,
    ACCOUNT_ANONYMIZED,

    // --- Métier ---
    RESERVATION_CREATED,
    RESERVATION_CONFIRMED,
    RESERVATION_CANCELLED,
    ORDER_PAID,
    ORDER_CANCELLED,
    // Paiement reçu sur une vente déjà annulée : rien n'est confirmé,
    // l'événement signale un remboursement à effectuer.
    PAYMENT_ON_CANCELLED_SALE,

    // --- Administration ---
    ACTIVITY_APPROVED,
    ACTIVITY_REJECTED,
    USER_MODIFIED_BY_ADMIN
}