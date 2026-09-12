export default {
  home: {
    subtitle: "Plateforme belge de loisirs",
    title: "Des loisirs à réserver et acheter en quelques clics",
    description:
      "Découvrez des activités, réservez vos créneaux et retrouvez des produits liés à vos loisirs depuis une même plateforme.",

    searchPlaceholder: "Rechercher une activité ou un produit",
    searchButton: "Chercher",
    noSearchResults: "Aucun résultat trouvé.",

    activitiesButton: "Voir les activités",
    shopButton: "Voir la boutique",

    featured: "À découvrir",
    from: "Dès",
    book: "Réserver",
    discover: "Découvrir",
    previous: "Précédent",
    next: "Suivant",
    slide: "Diapositive",

    activity: "Activité",
    product: "Produit",

    selection: "Bel'Loisirs",
    suggestionsTitle: "Suggestions du moment",
    viewAll: "Voir toutes les activités",
    view: "Voir",

    loading: "Chargement des suggestions...",
    emptyTitle: "Les suggestions arrivent bientôt",
    emptyText:
      "Consultez les activités et la boutique pour découvrir les offres disponibles.",

    categories: {
      wellness: "Wellness",
      sport: "Sport",
      bars: "Bars",
      culture: "Culture",
      shop: "Boutique"
    }
  },

  activityList: {
    title: "Activités et expériences",
    subtitle:
      "Découvrez et comparez les activités disponibles sur la plateforme.",

    loading: "Chargement des activités...",
    details: "Voir",
    empty: "Aucune activité ne correspond à vos critères.",
    loadError: "Impossible de charger les activités.",

    results: "Résultats des activités",
    resultsCount: "{count} activité(s) disponible(s)",

    filters: {
      title: "Filtres",
      reset: "Réinitialiser",
      all: "Tous",

      type: "Type d'activité",
      location: "Ville / zone",
      partner: "Partenaire",
      price: "Prix",

      minPrice: "Min.",
      maxPrice: "Max.",

      availableOnly: "Disponible uniquement"
    }
  },

  activityDetail: {
    back: "Retour aux activités",
    loading: "Chargement de l’activité...",

    price: "Prix",
    duration: "Durée",
    minutes: "minutes",
    partner: "Partenaire",
    partnerId: "ID partenaire",

    sessionsTitle: "Créneaux disponibles",
    sessionsLoading: "Chargement des créneaux...",
    sessionsEmpty:
      "Aucun créneau n’est proposé pour cette activité pour le moment.",

    remainingSeatsPlural: "places restantes",
    remainingSeatsSingular: "place restante",

    quantity: "Quantité",
    redirecting: "Redirection...",
    pay: "Payer",
    cancel: "Annuler",
    book: "Réserver",
    bookingUntil: "Réservation jusqu’au",

    full: "Complet",
    closedOn: "Réservations clôturées le {date}",

    invalidQuantity: "Quantité invalide.",
    bookingError: "Impossible de créer la réservation.",
    loadError: "Impossible de charger le détail de l’activité."
  },

  privacy: {
    title: "Politique de confidentialité",
    subtitle:
      "Cette page explique comment Bel'Loisirs collecte et traite vos données personnelles, conformément au Règlement Général sur la Protection des Données (RGPD).",

    controllerTitle: "1. Responsable du traitement",
    controllerText:
      "[À COMPLÉTER : nom de l'entité responsable, adresse, email de contact]",

    dataTitle: "2. Données collectées",
    dataIntro:
      "Lors de la création d'un compte et de l'utilisation de la plateforme, les données suivantes sont collectées :",

    dataIdentity: "Identité : nom, prénom, date de naissance",
    dataContact: "Coordonnées : adresse e-mail, numéro de téléphone",
    dataPassword:
      "Mot de passe (stocké de façon chiffrée, jamais en clair)",
    dataProfilePicture: "Photo de profil (facultative)",
    dataHistory:
      "Historique des commandes et réservations effectuées sur la plateforme",
    dataPartners:
      "Pour les partenaires : nom de l'entreprise, adresse(s) professionnelle(s), logo",

    paymentData:
      "Les données de paiement (numéro de carte bancaire, etc.) ne sont jamais stockées par Bel'Loisirs : elles sont traitées directement par notre prestataire de paiement, Stripe.",

    purposesTitle: "3. Finalités du traitement",
    purposeAccount: "Gestion du compte utilisateur et authentification",
    purposeReservations: "Réservation d'activités et achat de produits",
    purposePayments: "Traitement des paiements (via Stripe)",
    purposeCommunication:
      "Communication liée aux commandes et réservations",

    legalBasisTitle: "4. Base légale",
    legalBasisText:
      "Le traitement repose sur l'exécution du contrat (création de compte, achats, réservations) et sur votre consentement explicite, recueilli lors de l'inscription.",

    retentionTitle: "5. Durée de conservation",
    retentionText:
      "[À COMPLÉTER : durée de conservation des données après suppression du compte / dernière activité]",

    recipientsTitle: "6. Destinataires des données",
    recipientsText:
      "Vos données ne sont partagées qu'avec les prestataires nécessaires au fonctionnement de la plateforme : Stripe (paiement) et notre hébergeur. [À COMPLÉTER : nom de l'hébergeur une fois le déploiement final confirmé]",

    rightsTitle: "7. Vos droits",
    rightsText:
      "Conformément au RGPD, vous disposez d'un droit d'accès, de rectification, d'effacement, de limitation et de portabilité de vos données, ainsi que du droit de retirer votre consentement à tout moment.",
    rightsContact:
      "Pour exercer ces droits, contactez-nous à l'adresse suivante : [À COMPLÉTER : adresse email de contact].",
    complaint:
      "Vous disposez également du droit d'introduire une réclamation auprès de l'Autorité de protection des données (APD) en Belgique.",

    securityTitle: "8. Sécurité",
    securityText:
      "Les mots de passe sont chiffrés (BCrypt) et l'authentification repose sur des jetons JWT à durée limitée. Aucune donnée de carte bancaire ne transite par nos serveurs.",

    lastUpdate: "Dernière mise à jour : [À COMPLÉTER]"
  },

  productDetail: {
    back: "Retour à la boutique",
    loading: "Chargement du produit...",

    price: "Prix",
    stock: "Stock",
    available: "disponible(s)",
    status: "Statut",

    availableStatus: "Disponible",
    unavailableStatus: "Indisponible",

    addToCart: "Ajouter au panier",

    loadError: "Impossible de charger le détail du produit.",
    addedToCart: "Produit ajouté au panier !"
  },

  shop: {
    title: "Boutique",
    subtitle:
      "Découvrez les produits disponibles pour vos activités de loisirs.",

    loading: "Chargement des produits...",
    stock: "Stock",
    details: "Voir le détail",

    empty: "Aucun produit disponible.",
    loadError: "Impossible de charger les produits."
  }
};