export default {
  profile: {
    title: "Mon profil",
    subtitle: "Consultez et modifiez vos informations personnelles.",

    profileImageAlt: "Photo de profil",
    uploading: "Envoi...",
    editPhoto: "Modifier la photo",

    firstName: "Prénom",
    lastName: "Nom",
    phone: "Téléphone",
    optional: "Facultatif",
    email: "Email",
    emailHint: "L’adresse e-mail est utilisée comme identifiant de connexion et ne peut pas être modifiée.",
    roles: "Rôle(s)",

    saving: "Enregistrement...",
    save: "Enregistrer",
    cancel: "Annuler",

    deleteAccountTitle: "Supprimer mon compte",
    deleteAccountText: "La suppression du compte est définitive. Certaines données peuvent être conservées lorsqu’une obligation légale l’impose.",
    deleteAccount: "Supprimer mon compte",

    noUser: "Aucun utilisateur connecté.",

    confirmDeleteTitle: "Confirmer la suppression du compte",
    confirmDeleteText: "Pour confirmer votre demande de suppression, saisissez votre mot de passe.",
    password: "Mot de passe",
    deleting: "Suppression...",
    deletePermanently: "Supprimer définitivement",

    loadError: "Impossible de charger votre profil.",
    updateSuccess: "Profil mis à jour avec succès.",
    updateError: "Impossible de mettre à jour votre profil.",
    deleteError: "Impossible de supprimer votre compte.",
    photoSuccess: "Photo de profil mise à jour avec succès.",
    photoError: "Impossible de modifier la photo de profil.",

    rolesLabels: {
      MEMBER: "Membre",
      PARTNER: "Partenaire",
      ADMIN: "Administrateur"
    }
  },

  reservations: {
    title: "Mes réservations",
    subtitle: "Consultez vos réservations et leur statut.",

    loading: "Chargement des réservations...",

    reference: "Référence",
    activity: "Activité",
    session: "Session",
    places: "Places",
    total: "Total",
    status: "Statut",
    bookedAt: "Réservé le",
    action: "Action",

    cancelling: "Annulation...",
    cancel: "Annuler",
    cancellationClosed: "Annulation clôturée",

    empty: "Aucune réservation trouvée.",

    loadError: "Impossible de charger vos réservations.",
    cancelConfirmation: "Voulez-vous vraiment annuler la réservation {reference} ?",
    cancelSuccess: "La réservation {reference} a été annulée.",
    cancelError: "Impossible d’annuler cette réservation.",

    statuses: {
      PENDING: "En attente",
      CONFIRMED: "Confirmée",
      CANCELLED: "Annulée"
    }
  },

  cart: {
    title: "Mon panier",
    empty: "Votre panier est vide.",

    quantity: "Quantité :",
    subtotal: "Sous-total :",
    delete: "Supprimer",

    total: "Total :",
    clear: "Vider le panier",

    stripeRedirect: "Redirection vers Stripe...",
    payWithStripe: "Payer avec Stripe",

    paymentError: "Impossible de démarrer le paiement."
  }
};