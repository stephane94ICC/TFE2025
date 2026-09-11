export default {
  activities: {
    title: "Mes activités",
    subtitle: "Consultez vos activités et leur état de validation.",
    add: "Ajouter une activité",
    loading: "Chargement des activités...",

    activityTitle: "Titre",
    price: "Prix",
    duration: "Durée",
    status: "Statut",
    actions: "Actions",

    edit: "Modifier",
    manageImages: "Gérer les images",
    manageSessions: "Gérer les créneaux",
    rejectionReason: "Raison du refus",

    empty: "Aucune activité trouvée.",
    loadError: "Impossible de charger les activités.",

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

    note: "Après l’enregistrement, l’activité devra être validée par l’administrateur avant d’être visible publiquement.",

    title: "Titre",
    description: "Description",
    price: "Prix (€)",
    duration: "Durée (minutes)",
    minimumAge: "Âge minimum",
    equipmentInformation: "Informations sur l’équipement",

    saving: "Enregistrement...",
    save: "Enregistrer",
    cancel: "Annuler",

    saveError: "Impossible d’enregistrer l’activité."
  },

  activityImages: {
    title: "Images",
    loading: "Chargement...",

    uploading: "Envoi...",
    addImage: "Ajouter une image",

    imageAlt: "Image de l’activité",
    defaultImage: "Image par défaut",

    deleting: "Suppression...",
    delete: "Supprimer",

    empty: "Aucune image pour cette activité.",

    loadError: "Impossible de charger les images.",
    addSuccess: "Image ajoutée avec succès.",
    addError: "Impossible d’ajouter l’image.",
    deleteSuccess: "Image supprimée avec succès.",
    deleteError: "Impossible de supprimer l’image."
  },

  activityRejection: {
    title: "Raison du refus",
    noComment: "Aucun commentaire n’a été indiqué par l’administrateur.",
    close: "Fermer"
  },

  activitySessions: {
    title: "Créneaux",
    loading: "Chargement...",

    addTitle: "Ajouter un créneau",
    noLocation: "Vous devez d’abord enregistrer un lieu dans « Mes lieux ».",

    location: "Lieu",
    chooseLocation: "-- Choisir un lieu --",
    capacity: "Capacité",
    start: "Début",
    end: "Fin",
    bookingDeadline: "Clôture des réservations",

    deadline2Hours: "2 heures avant le début",
    deadline24Hours: "24 heures avant le début",
    deadline48Hours: "48 heures avant le début",
    deadline1Week: "1 semaine avant le début",

    saving: "Enregistrement...",
    add: "Ajouter le créneau",

    seats: "Places",
    deadline: "Clôture",
    status: "Statut",

    empty: "Aucun créneau pour cette activité.",

    loadError: "Impossible de charger les créneaux.",
    endBeforeStart: "La date de fin doit être après la date de début.",
    addSuccess: "Créneau ajouté avec succès.",
    addError: "Impossible d’ajouter le créneau.",

    statuses: {
      SCHEDULED: "Programmé",
      CANCELLED: "Annulé",
      COMPLETED: "Terminé"
    }
  },

  addressForm: {
    editTitle: "Modifier l’adresse",
    addTitle: "Ajouter une adresse",

    addressType: "Type d’adresse",
    legalAddress: "Adresse légale",
    contactAddress: "Adresse de contact",

    street: "Rue",
    houseNumber: "Numéro",
    box: "Boîte",
    postalCode: "Code postal",
    city: "Ville",
    country: "Pays",

    saving: "Enregistrement...",
    save: "Enregistrer",
    cancel: "Annuler"
  },

  addressList: {
    title: "Mes adresses",
    subtitle: "Gérez les adresses légales et de contact de votre entreprise.",

    add: "Ajouter une adresse",
    loading: "Chargement des adresses...",

    legalAddress: "Adresse légale",
    contactAddress: "Adresse de contact",
    box: "boîte",

    edit: "Modifier",
    deleting: "Suppression...",
    delete: "Supprimer"
  },

  companyForm: {
    title: "Informations générales",
    subtitle: "Coordonnées professionnelles et informations officielles.",

    edit: "Modifier",

    name: "Nom",
    phone: "Téléphone",
    email: "Email professionnel",
    website: "Site web",
    enterpriseNumber: "Numéro d’entreprise",
    vatNumber: "Numéro de TVA",
    description: "Description",

    notProvided: "Non renseigné",

    editTitle: "Modifier les informations",
    companyName: "Nom de l’entreprise",

    readonlyHelp: "Le numéro d’entreprise et le numéro de TVA ne peuvent pas être modifiés ici.",

    saving: "Enregistrement...",
    save: "Enregistrer",
    cancel: "Annuler"
  },

  locationForm: {
    editTitle: "Modifier le lieu",
    addTitle: "Ajouter un lieu",

    name: "Nom du lieu",
    street: "Rue",
    houseNumber: "Numéro",
    box: "Boîte",
    postalCode: "Code postal",
    city: "Ville",
    country: "Pays",

    gpsHint: "Coordonnées GPS (facultatif) — dans Google Maps, clic droit sur le lieu : les coordonnées se copient automatiquement.",

    latitude: "Latitude",
    longitude: "Longitude",
    accessInformation: "Informations d’accès",

    saving: "Enregistrement...",
    save: "Enregistrer",
    cancel: "Annuler"
  },

  locations: {
    title: "Mes lieux",
    subtitle: "Lieux où se déroulent vos activités. Un lieu peut être réutilisé pour plusieurs créneaux.",

    add: "Ajouter un lieu",
    loading: "Chargement des lieux...",

    name: "Nom",
    address: "Adresse",
    city: "Ville",
    coordinates: "Coordonnées",
    access: "Accès",
    actions: "Actions",

    notProvided: "Non renseignées",
    edit: "Modifier",

    empty: "Aucun lieu enregistré.",

    loadError: "Impossible de charger les lieux.",
    updateSuccess: "Lieu modifié avec succès.",
    addSuccess: "Lieu ajouté avec succès.",
    saveError: "Impossible d’enregistrer le lieu.",

    boxShort: "bte"
  },

  logoForm: {
    logoAlt: "Logo de l’entreprise",
    uploading: "Envoi...",
    editLogo: "Modifier le logo"
  },

  page: {
    title: "Mon entreprise",
    subtitle: "Gérez les informations professionnelles de votre entreprise.",

    loading: "Chargement...",
    logoTitle: "Logo de l’entreprise",

    companyLoadError: "Impossible de charger les informations de l’entreprise.",

    loadError: "Impossible de charger les informations.",
    addressesLoadError: "Impossible de charger les adresses.",

    profileUpdateSuccess: "Informations modifiées avec succès.",
    profileUpdateError: "Impossible de modifier les informations.",

    logoUpdateSuccess: "Logo modifié avec succès.",
    logoUpdateError: "Impossible de modifier le logo.",

    addressUpdateSuccess: "Adresse modifiée avec succès.",
    addressAddSuccess: "Adresse ajoutée avec succès.",
    addressSaveError: "Impossible d’enregistrer l’adresse.",

    confirmAddressDelete: "Supprimer cette adresse ?",
    addressDeleteSuccess: "Adresse supprimée avec succès.",
    addressDeleteError: "Impossible de supprimer l’adresse."
  }
};