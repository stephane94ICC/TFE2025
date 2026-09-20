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
    resultsCount: "Aucune activité disponible | 1 activité disponible | {count} activités disponibles",

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
    minimumAge: "Âge minimum",
    ageValue: "{age} ans",
    allAges: "Tous âges",
    equipment: "Équipement",
    categories: "Catégories",
    cities: "Villes",

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
      "Bel'Loisirs est une application réalisée dans le cadre d'un travail de fin d'études en informatique de gestion à l'Institut des Carrières Commerciales (ICC), Bruxelles. Contact : contact{'@'}belloisirs.example (adresse fictive — projet de fin d'études).",

    dataTitle: "2. Données collectées",
    dataIntro:
      "Lors de la création d'un compte et de l'utilisation de la plateforme, les données suivantes sont collectées :",

    dataIdentity: "Identité : nom, prénom",
    dataContact: "Coordonnées : adresse e-mail, numéro de téléphone (facultatif, via le profil)",
    dataPassword:
      "Mot de passe, stocké sous forme hachée (BCrypt), jamais en clair ni de façon réversible",
    dataProfilePicture: "Photo de profil (facultative)",
    dataHistory:
      "Historique des commandes et réservations effectuées sur la plateforme, avec les données de facturation associées",
    dataPartners:
      "Pour les partenaires : nom de l'entreprise, adresse(s) professionnelle(s), logo",
    dataSecurityLog:
      "Journal de sécurité : adresse IP, adresse e-mail et type d'action (connexion, inscription, réservation, paiement)",

    paymentData:
      "Les données de paiement (numéro de carte bancaire, etc.) ne sont jamais stockées par Bel'Loisirs : elles sont traitées directement par notre prestataire de paiement, Stripe.",

    purposesTitle: "3. Finalités du traitement",
    purposeAccount: "Gestion du compte utilisateur et authentification",
    purposeReservations: "Réservation d'activités et achat de produits",
    purposePayments: "Traitement des paiements (via Stripe)",
    purposeSecurity:
      "Sécurité de la plateforme et traçabilité des opérations sensibles (journal d'audit)",

    legalBasisTitle: "4. Base légale",
    legalBasisText:
      "Le traitement repose sur l'exécution du contrat pour la gestion du compte, des réservations et des achats ; sur une obligation légale pour la conservation des données de facturation ; et sur l'intérêt légitime de Bel'Loisirs pour le journal de sécurité (prévention des fraudes et des accès abusifs).",

    retentionTitle: "5. Durée de conservation",
    retentionText:
      "Les données du compte sont conservées jusqu'à sa suppression ; elles sont alors pseudonymisées. Les données de facturation sont conservées 10 ans, conformément aux obligations comptables belges. Le journal de sécurité est conservé 12 mois.",

    recipientsTitle: "6. Destinataires des données",
    recipientsText:
      "Vos données ne sont partagées qu'avec les prestataires nécessaires au fonctionnement de la plateforme : Stripe (paiement) et Railway (hébergement).",

    rightsTitle: "7. Vos droits",
    rightsText:
      "Conformément au RGPD, vous disposez d'un droit d'accès, de rectification, d'effacement, de limitation et de portabilité de vos données, ainsi que d'un droit d'opposition au traitement fondé sur l'intérêt légitime.",
    rightsContact:
      "Pour exercer ces droits, contactez-nous à l'adresse suivante : contact{'@'}belloisirs.example (adresse fictive — projet de fin d'études).",
    complaint:
      "Vous disposez également du droit d'introduire une réclamation auprès de l'Autorité de protection des données (APD) en Belgique.",

    securityTitle: "8. Sécurité",
    securityText:
      "Les mots de passe sont hachés (BCrypt) et l'authentification repose sur des jetons JWT à durée limitée. Aucune donnée de carte bancaire ne transite par nos serveurs.",

    cookiesTitle: "9. Cookies et stockage local",
    cookiesText:
      "Bel'Loisirs n'utilise aucun cookie, aucun outil de mesure d'audience ni aucun traceur publicitaire, et les polices de caractères sont hébergées sur nos propres serveurs. Le navigateur conserve uniquement, dans son stockage local, les informations strictement nécessaires au service que vous demandez : la langue choisie, le contenu du panier et vos informations de connexion. Ce stockage étant indispensable au fonctionnement de la plateforme, il ne requiert pas de consentement. Les informations de connexion sont effacées lors de la déconnexion ; l'ensemble peut être supprimé à tout moment via les paramètres de votre navigateur.",

    lastUpdate: "Dernière mise à jour : 19 septembre 2026"
  },

  productDetail: {
    back: "Retour à la boutique",
    loading: "Chargement du produit...",

    price: "Prix",
    stock: "Stock",
    available: "Épuisé | 1 disponible | {count} disponibles",
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