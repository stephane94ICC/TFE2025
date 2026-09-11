export default {
  dashboard: {
    title: "Espace administrateur",
    introduction: "Bienvenue dans le tableau de bord administrateur. Cette page permet d’accéder aux principales zones de gestion de la plateforme.",

    usersTitle: "Utilisateurs",
    usersDescription: "Consulter et gérer les comptes utilisateurs inscrits sur la plateforme.",

    activitiesTitle: "Activités",
    activitiesDescription: "Gérer les activités proposées par les partenaires.",

    productsTitle: "Produits",
    productsDescription: "Gérer les produits disponibles dans la boutique.",

    activityLogTitle: "Journal d'activité",
    activityLogDescription: "Consulter l’historique des connexions et des actions réalisées sur la plateforme.",

    partnersTitle: "Partenaires",
    partnersDescription: "Consulter et gérer les partenaires de la plateforme.",

    access: "Accéder",
    comingSoon: "Bientôt disponible"
  },

  activities: {
    title: "Gestion des activités",
    subtitle: "Consultez les activités et validez les propositions des partenaires.",
    add: "Ajouter une activité",
    loading: "Chargement des activités...",

    id: "ID",
    activityTitle: "Titre",
    price: "Prix",
    partner: "Partenaire",
    status: "Statut",
    actions: "Actions",

    partnerFallback: "Partenaire #{id}",

    edit: "Modifier",
    images: "Images",
    approve: "Approuver",
    reject: "Refuser",
    disable: "Désactiver",

    approveAction: "approuver",
    disableAction: "désactiver",
    confirmReview: "Voulez-vous vraiment {action} cette activité ?",

    empty: "Aucune activité trouvée.",

    loadError: "Impossible de charger les activités.",
    updateSuccess: "Le statut de l’activité a été mis à jour.",
    updateError: "Impossible de modifier le statut de l’activité.",

    statuses: {
      PENDING_REVIEW: "En attente",
      APPROVED: "Approuvée",
      REJECTED: "Refusée",
      DISABLED: "Désactivée"
    }
  },

  activityForm: {
    editTitle: "Modifier une activité",
    addTitle: "Ajouter une activité",

    title: "Titre",
    description: "Description",
    price: "Prix",
    city: "Ville",
    partnerId: "ID partenaire",

    cancel: "Annuler",
    save: "Enregistrer",

    loadError: "Impossible de charger l’activité.",
    saveError: "Impossible d’enregistrer l’activité."
  },

  activityImages: {
    title: "Images de l’activité",
    loading: "Chargement...",
    activityLabel: "Activité :",

    uploading: "Envoi...",
    addImage: "Ajouter une image",

    imageAlt: "Image de l’activité",
    defaultImage: "Image par défaut",

    deleting: "Suppression...",
    delete: "Supprimer",

    backToList: "Retour à la liste",

    loadError: "Impossible de charger les images.",
    addSuccess: "Image ajoutée avec succès.",
    addError: "Impossible d’ajouter l’image.",
    deleteSuccess: "Image supprimée avec succès.",
    deleteError: "Impossible de supprimer l’image."
  },

  activityReview: {
    title: "Refuser l’activité",
    instruction: "Indiquez la raison du refus. Le partenaire pourra consulter ce commentaire.",
    placeholder: "Raison du refus...",
    confirm: "Confirmer le refus",
    cancel: "Annuler",
    requiredError: "La raison du refus est obligatoire."
  },

  activityLogs: {
    title: "Journal d'activité",
    subtitle: "Historique des événements enregistrés sur la plateforme.",
    resetFilters: "Réinitialiser les filtres",

    filters: "Filtres",
    eventType: "Type d'événement",
    allTypes: "Tous les types",
    email: "Adresse e-mail",
    partialSearch: "Recherche partielle",
    from: "Du",
    to: "Au",
    search: "Rechercher",

    events: "Événements",
    entryCount: "{count} entrée(s)",
    loading: "Chargement du journal...",

    date: "Date",
    event: "Événement",
    user: "Utilisateur",
    details: "Détails",
    ipAddress: "Adresse IP",

    empty: "Aucun événement ne correspond aux critères.",

    previous: "Précédent",
    page: "Page {current} sur {total}",
    next: "Suivant",

    loadError: "Impossible de charger le journal d'activité.",

    eventTypes: {
      LOGIN_SUCCESS: "Connexion réussie",
      LOGIN_FAILURE: "Échec de connexion",
      REGISTER: "Inscription",
      ACCESS_DENIED_INACTIVE_ACCOUNT: "Accès refusé — compte désactivé",
      PROFILE_UPDATED: "Profil modifié",
      ACCOUNT_DELETION_REQUESTED: "Désinscription demandée",
      ACCOUNT_ANONYMIZED: "Compte anonymisé",
      RESERVATION_CREATED: "Réservation créée",
      RESERVATION_CONFIRMED: "Réservation confirmée",
      RESERVATION_CANCELLED: "Réservation annulée",
      ORDER_PAID: "Commande payée",
      ACTIVITY_APPROVED: "Activité approuvée",
      ACTIVITY_REJECTED: "Activité refusée",
      USER_MODIFIED_BY_ADMIN: "Utilisateur modifié par un administrateur"
    }
  },

  products: {
    title: "Gestion des produits",
    description: "Cette page permet à l’administrateur de consulter les produits de la boutique.",
    loading: "Chargement des produits...",

    listTitle: "Liste des produits",
    productCount: "{count} produit(s)",
    add: "Ajouter un produit",

    id: "ID",
    name: "Nom",
    price: "Prix",
    stock: "Stock",
    active: "Actif",
    actions: "Actions",

    yes: "Oui",
    no: "Non",

    edit: "Modifier",
    manageImages: "Gérer les images",
    deleting: "Suppression...",
    delete: "Supprimer",

    empty: "Aucun produit trouvé.",

    loadError: "Impossible de charger les produits.",
    confirmDelete: "Voulez-vous vraiment supprimer le produit \"{name}\" ?",
    deleteSuccess: "Produit supprimé avec succès.",
    deleteError: "Impossible de supprimer le produit."
  },

  productForm: {
    editTitle: "Modifier un produit",
    addTitle: "Ajouter un produit",

    editDescription: "Ce formulaire permet à l’administrateur de modifier un produit existant.",
    addDescription: "Ce formulaire permet à l’administrateur d’ajouter un nouveau produit dans la boutique.",

    loading: "Chargement du produit...",

    name: "Nom du produit",
    namePlaceholder: "Exemple : Tapis de yoga",

    description: "Description",
    descriptionPlaceholder: "Description du produit",

    price: "Prix",
    stock: "Stock",

    activeLabel: "Produit actif dans la boutique",

    cancel: "Annuler",
    saving: "Enregistrement...",
    editButton: "Modifier le produit",
    addButton: "Ajouter le produit",

    loadError: "Impossible de charger le produit.",
    editError: "Impossible de modifier le produit.",
    addError: "Impossible d’ajouter le produit."
  },

  productImages: {
    title: "Images du produit",
    loading: "Chargement...",
    productLabel: "Produit :",

    uploading: "Envoi...",
    addImage: "Ajouter une image",

    imageAlt: "Image du produit",
    defaultImage: "Image par défaut",

    deleting: "Suppression...",
    delete: "Supprimer",

    backToList: "Retour à la liste",

    loadError: "Impossible de charger les images.",
    addSuccess: "Image ajoutée avec succès.",
    addError: "Impossible d’ajouter l’image.",
    deleteSuccess: "Image supprimée avec succès.",
    deleteError: "Impossible de supprimer l’image."
  },

  users: {
    title: "Gestion des utilisateurs",
    subtitle: "Liste des utilisateurs enregistrés sur la plateforme.",

    cancel: "Annuler",
    addUser: "Ajouter un utilisateur",

    createTitle: "Créer un utilisateur",
    editTitle: "Modifier l’utilisateur",
    listTitle: "Liste des utilisateurs",

    id: "ID",
    email: "Email",
    firstName: "Prénom",
    lastName: "Nom",
    password: "Mot de passe",
    newPassword: "Nouveau mot de passe",
    role: "Rôle",
    rgpd: "RGPD",
    actions: "Actions",

    emailPlaceholder: "exemple@email.com",
    firstNamePlaceholder: "Prénom",
    lastNamePlaceholder: "Nom",
    passwordPlaceholder: "Mot de passe",
    unchangedPasswordPlaceholder: "Laisser vide si inchangé",

    rgpdInfo: "Le consentement RGPD est donné par l’utilisateur lors de son inscription.",

    create: "Créer",
    save: "Enregistrer",

    loading: "Chargement des utilisateurs...",

    yes: "Oui",
    no: "Non",

    edit: "Modifier",
    delete: "Supprimer",

    empty: "Aucun utilisateur trouvé.",

    loadError: "Impossible de charger les utilisateurs.",
    requiredCreate: "Veuillez remplir tous les champs.",
    createSuccess: "Utilisateur créé avec succès.",
    createError: "Erreur lors de la création de l’utilisateur.",

    requiredEdit: "Veuillez remplir l’email, le prénom et le nom.",
    updateSuccess: "Utilisateur modifié avec succès.",
    updateError: "Erreur lors de la modification de l’utilisateur.",

    confirmDelete: "Voulez-vous vraiment supprimer cet utilisateur ?",
    deleteSuccess: "Utilisateur supprimé avec succès.",
    deleteError: "Erreur lors de la suppression de l’utilisateur.",

    roles: {
      MEMBER: "Membre",
      PARTNER: "Partenaire",
      ADMIN: "Administrateur"
    }
  }
};