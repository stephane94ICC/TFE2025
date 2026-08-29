export const ACTIVITY_EVENT_LABELS = {
    LOGIN_SUCCESS: "Connexion réussie",
    LOGIN_FAILURE: "Échec de connexion",
    REGISTER: "Inscription",
    ACCESS_DENIED_INACTIVE_ACCOUNT: "Accès refusé — compte désactivé",
    PROFILE_UPDATED: "Profil modifié",
    ACCOUNT_DELETION_REQUESTED: "Désinscription demandée",
    ACCOUNT_ANONYMIZED: "Compte anonymisé",
    RESERVATION_CREATED: "Réservation créée",
    RESERVATION_CANCELLED: "Réservation annulée",
    ORDER_PAID: "Commande payée",
    ACTIVITY_APPROVED: "Activité approuvée",
    ACTIVITY_REJECTED: "Activité refusée",
    USER_MODIFIED_BY_ADMIN: "Utilisateur modifié par un administrateur"
};

export function formatEventType(eventType) {
    return ACTIVITY_EVENT_LABELS[eventType] || eventType;
}