export default {
  dashboard: {
    title: "Beheerdersruimte",
    introduction: "Welkom op het beheerdersdashboard. Via deze pagina krijgt u toegang tot de belangrijkste beheeronderdelen van het platform.",

    usersTitle: "Gebruikers",
    usersDescription: "Bekijk en beheer de gebruikersaccounts die op het platform zijn geregistreerd.",

    activitiesTitle: "Activiteiten",
    activitiesDescription: "Beheer de activiteiten die door partners worden aangeboden.",

    productsTitle: "Producten",
    productsDescription: "Beheer de producten die beschikbaar zijn in de winkel.",

    activityLogTitle: "Activiteitenlogboek",
    activityLogDescription: "Bekijk de geschiedenis van aanmeldingen en acties die op het platform zijn uitgevoerd.",

    partnersTitle: "Partners",
    partnersDescription: "Bekijk en beheer de partners van het platform.",

    access: "Openen",
    comingSoon: "Binnenkort beschikbaar"
  },

  activities: {
    title: "Activiteitenbeheer",
    subtitle: "Bekijk de activiteiten en valideer de voorstellen van partners.",
    add: "Activiteit toevoegen",
    loading: "Activiteiten laden...",

    id: "ID",
    activityTitle: "Titel",
    price: "Prijs",
    partner: "Partner",
    status: "Status",
    actions: "Acties",

    partnerFallback: "Partner #{id}",

    edit: "Wijzigen",
    images: "Afbeeldingen",
    approve: "Goedkeuren",
    reject: "Weigeren",
    disable: "Deactiveren",

    approveAction: "goedkeuren",
    disableAction: "deactiveren",
    confirmReview: "Wilt u deze activiteit echt {action}?",

    empty: "Geen activiteit gevonden.",

    loadError: "De activiteiten konden niet worden geladen.",
    updateSuccess: "De status van de activiteit is bijgewerkt.",
    updateError: "De status van de activiteit kon niet worden gewijzigd.",

    statuses: {
      PENDING_REVIEW: "In afwachting",
      APPROVED: "Goedgekeurd",
      REJECTED: "Geweigerd",
      DISABLED: "Gedeactiveerd"
    }
  },

  activityForm: {
    editTitle: "Activiteit wijzigen",
    addTitle: "Activiteit toevoegen",

    title: "Titel",
    description: "Beschrijving",
    price: "Prijs",
    city: "Stad",
    partnerId: "Partner-ID",

    cancel: "Annuleren",
    save: "Opslaan",

    loadError: "De activiteit kon niet worden geladen.",
    saveError: "De activiteit kon niet worden opgeslagen."
  },

  activityImages: {
    title: "Afbeeldingen van de activiteit",
    loading: "Laden...",
    activityLabel: "Activiteit:",

    uploading: "Uploaden...",
    addImage: "Afbeelding toevoegen",

    imageAlt: "Afbeelding van de activiteit",
    defaultImage: "Standaardafbeelding",

    deleting: "Verwijderen...",
    delete: "Verwijderen",

    backToList: "Terug naar de lijst",

    loadError: "De afbeeldingen konden niet worden geladen.",
    addSuccess: "Afbeelding succesvol toegevoegd.",
    addError: "De afbeelding kon niet worden toegevoegd.",
    deleteSuccess: "Afbeelding succesvol verwijderd.",
    deleteError: "De afbeelding kon niet worden verwijderd."
  },

  activityReview: {
    title: "Activiteit weigeren",
    instruction: "Geef de reden voor de weigering aan. De partner kan deze opmerking raadplegen.",
    placeholder: "Reden voor weigering...",
    confirm: "Weigering bevestigen",
    cancel: "Annuleren",
    requiredError: "De reden voor de weigering is verplicht."
  },

  activityLogs: {
    title: "Activiteitenlogboek",
    subtitle: "Geschiedenis van de gebeurtenissen die op het platform zijn geregistreerd.",
    resetFilters: "Filters opnieuw instellen",

    filters: "Filters",
    eventType: "Type gebeurtenis",
    allTypes: "Alle types",
    email: "E-mailadres",
    partialSearch: "Gedeeltelijk zoeken",
    from: "Van",
    to: "Tot",
    search: "Zoeken",

    events: "Gebeurtenissen",
    entryCount: "{count} item(s)",
    loading: "Logboek laden...",

    date: "Datum",
    event: "Gebeurtenis",
    user: "Gebruiker",
    details: "Details",
    ipAddress: "IP-adres",

    empty: "Geen gebeurtenis voldoet aan de criteria.",

    previous: "Vorige",
    page: "Pagina {current} van {total}",
    next: "Volgende",

    loadError: "Het activiteitenlogboek kon niet worden geladen.",

    eventTypes: {
      LOGIN_SUCCESS: "Aanmelding geslaagd",
      LOGIN_FAILURE: "Aanmelding mislukt",
      REGISTER: "Registratie",
      ACCESS_DENIED_INACTIVE_ACCOUNT: "Toegang geweigerd — account gedeactiveerd",
      PROFILE_UPDATED: "Profiel gewijzigd",
      ACCOUNT_DELETION_REQUESTED: "Uitschrijving aangevraagd",
      ACCOUNT_ANONYMIZED: "Account geanonimiseerd",
      RESERVATION_CREATED: "Reservering aangemaakt",
      RESERVATION_CONFIRMED: "Reservering bevestigd",
      RESERVATION_CANCELLED: "Reservering geannuleerd",
      ORDER_PAID: "Bestelling betaald",
      ACTIVITY_APPROVED: "Activiteit goedgekeurd",
      ACTIVITY_REJECTED: "Activiteit geweigerd",
      USER_MODIFIED_BY_ADMIN: "Gebruiker gewijzigd door een beheerder"
    }
  },

  products: {
    title: "Productbeheer",
    description: "Via deze pagina kan de beheerder de producten van de winkel bekijken.",
    loading: "Producten laden...",

    listTitle: "Productlijst",
    productCount: "{count} product(en)",
    add: "Product toevoegen",

    id: "ID",
    name: "Naam",
    price: "Prijs",
    stock: "Voorraad",
    active: "Actief",
    actions: "Acties",

    yes: "Ja",
    no: "Nee",

    edit: "Wijzigen",
    manageImages: "Afbeeldingen beheren",
    deleting: "Verwijderen...",
    delete: "Verwijderen",

    empty: "Geen product gevonden.",

    loadError: "De producten konden niet worden geladen.",
    confirmDelete: "Wilt u het product \"{name}\" echt verwijderen?",
    deleteSuccess: "Product succesvol verwijderd.",
    deleteError: "Het product kon niet worden verwijderd."
  },

  productForm: {
    editTitle: "Product wijzigen",
    addTitle: "Product toevoegen",

    editDescription: "Via dit formulier kan de beheerder een bestaand product wijzigen.",
    addDescription: "Via dit formulier kan de beheerder een nieuw product aan de winkel toevoegen.",

    loading: "Product laden...",

    name: "Productnaam",
    namePlaceholder: "Voorbeeld: Yogamat",

    description: "Beschrijving",
    descriptionPlaceholder: "Productbeschrijving",

    price: "Prijs",
    stock: "Voorraad",

    activeLabel: "Product actief in de winkel",

    cancel: "Annuleren",
    saving: "Opslaan...",
    editButton: "Product wijzigen",
    addButton: "Product toevoegen",

    loadError: "Het product kon niet worden geladen.",
    editError: "Het product kon niet worden gewijzigd.",
    addError: "Het product kon niet worden toegevoegd."
  },

  productImages: {
    title: "Afbeeldingen van het product",
    loading: "Laden...",
    productLabel: "Product:",

    uploading: "Uploaden...",
    addImage: "Afbeelding toevoegen",

    imageAlt: "Afbeelding van het product",
    defaultImage: "Standaardafbeelding",

    deleting: "Verwijderen...",
    delete: "Verwijderen",

    backToList: "Terug naar de lijst",

    loadError: "De afbeeldingen konden niet worden geladen.",
    addSuccess: "Afbeelding succesvol toegevoegd.",
    addError: "De afbeelding kon niet worden toegevoegd.",
    deleteSuccess: "Afbeelding succesvol verwijderd.",
    deleteError: "De afbeelding kon niet worden verwijderd."
  },

  users: {
    title: "Gebruikersbeheer",
    subtitle: "Lijst van gebruikers die op het platform zijn geregistreerd.",

    cancel: "Annuleren",
    addUser: "Gebruiker toevoegen",

    createTitle: "Gebruiker aanmaken",
    editTitle: "Gebruiker wijzigen",
    listTitle: "Gebruikerslijst",

    id: "ID",
    email: "E-mail",
    firstName: "Voornaam",
    lastName: "Naam",
    password: "Wachtwoord",
    newPassword: "Nieuw wachtwoord",
    role: "Rol",
    rgpd: "AVG",
    actions: "Acties",

    emailPlaceholder: "voorbeeld@email.com",
    firstNamePlaceholder: "Voornaam",
    lastNamePlaceholder: "Naam",
    passwordPlaceholder: "Wachtwoord",
    unchangedPasswordPlaceholder: "Leeg laten indien ongewijzigd",

    rgpdInfo: "De AVG-toestemming wordt door de gebruiker gegeven bij de registratie.",

    create: "Aanmaken",
    save: "Opslaan",

    loading: "Gebruikers laden...",

    yes: "Ja",
    no: "Nee",

    edit: "Wijzigen",
    delete: "Verwijderen",

    empty: "Geen gebruiker gevonden.",

    loadError: "De gebruikers konden niet worden geladen.",
    requiredCreate: "Vul alle velden in.",
    createSuccess: "Gebruiker succesvol aangemaakt.",
    createError: "Fout bij het aanmaken van de gebruiker.",

    requiredEdit: "Vul het e-mailadres, de voornaam en de naam in.",
    updateSuccess: "Gebruiker succesvol gewijzigd.",
    updateError: "Fout bij het wijzigen van de gebruiker.",

    confirmDelete: "Wilt u deze gebruiker echt verwijderen?",
    deleteSuccess: "Gebruiker succesvol verwijderd.",
    deleteError: "Fout bij het verwijderen van de gebruiker.",

    roles: {
      MEMBER: "Lid",
      PARTNER: "Partner",
      ADMIN: "Beheerder"
    }
  }
};