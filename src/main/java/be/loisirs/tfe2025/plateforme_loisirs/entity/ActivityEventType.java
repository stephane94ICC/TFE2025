package be.loisirs.tfe2025.plateforme_loisirs.entity;

public enum ActivityEventType {

    // --- Sécurité / authentification ---
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    REGISTER,
    ACCESS_DENIED_INACTIVE_ACCOUNT,
    PASSWORD_CHANGED,
    PASSWORD_CHANGE_FAILURE,

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
    PAYMENT_ON_CANCELLED_SALE,

    // --- Administration ---
    ACTIVITY_APPROVED,
    ACTIVITY_REJECTED,
    USER_MODIFIED_BY_ADMIN
}