export default {
  activities: {
    title: "Mijn activiteiten",
    subtitle: "Bekijk uw activiteiten en hun validatiestatus.",
    add: "Activiteit toevoegen",
    loading: "Activiteiten laden...",

    activityTitle: "Titel",
    price: "Prijs",
    duration: "Duur",
    status: "Status",
    actions: "Acties",

    edit: "Wijzigen",
    manageImages: "Afbeeldingen beheren",
    manageSessions: "Tijdsloten beheren",
    rejectionReason: "Reden van weigering",

    empty: "Geen activiteiten gevonden.",
    loadError: "De activiteiten konden niet worden geladen.",

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

    note: "Na het opslaan moet de activiteit door de beheerder worden goedgekeurd voordat ze publiek zichtbaar is.",

    title: "Titel",
    description: "Beschrijving",
    price: "Prijs (€)",
    duration: "Duur (minuten)",
    minimumAge: "Minimumleeftijd",
    equipmentInformation: "Informatie over de uitrusting",

    saving: "Opslaan...",
    save: "Opslaan",
    cancel: "Annuleren",

    saveError: "De activiteit kon niet worden opgeslagen."
  },

  activityImages: {
    title: "Afbeeldingen",
    loading: "Laden...",

    uploading: "Uploaden...",
    addImage: "Afbeelding toevoegen",

    imageAlt: "Afbeelding van de activiteit",
    defaultImage: "Standaardafbeelding",

    deleting: "Verwijderen...",
    delete: "Verwijderen",

    empty: "Geen afbeeldingen voor deze activiteit.",

    loadError: "De afbeeldingen konden niet worden geladen.",
    addSuccess: "Afbeelding succesvol toegevoegd.",
    addError: "De afbeelding kon niet worden toegevoegd.",
    deleteSuccess: "Afbeelding succesvol verwijderd.",
    deleteError: "De afbeelding kon niet worden verwijderd."
  },

  activityRejection: {
    title: "Reden van weigering",
    noComment: "De beheerder heeft geen opmerking opgegeven.",
    close: "Sluiten"
  },

  activitySessions: {
    title: "Tijdsloten",
    loading: "Laden...",

    addTitle: "Tijdslot toevoegen",
    noLocation: "U moet eerst een locatie registreren in « Mijn locaties ».",

    location: "Locatie",
    chooseLocation: "-- Kies een locatie --",
    capacity: "Capaciteit",
    start: "Begin",
    end: "Einde",
    bookingDeadline: "Sluiting van de reservaties",

    deadline2Hours: "2 uur voor het begin",
    deadline24Hours: "24 uur voor het begin",
    deadline48Hours: "48 uur voor het begin",
    deadline1Week: "1 week voor het begin",

    saving: "Opslaan...",
    add: "Tijdslot toevoegen",

    seats: "Plaatsen",
    deadline: "Sluiting",
    status: "Status",

    empty: "Geen tijdsloten voor deze activiteit.",

    loadError: "De tijdsloten konden niet worden geladen.",
    endBeforeStart: "De einddatum moet na de begindatum liggen.",
    addSuccess: "Tijdslot succesvol toegevoegd.",
    addError: "Het tijdslot kon niet worden toegevoegd.",

    statuses: {
      SCHEDULED: "Gepland",
      CANCELLED: "Geannuleerd",
      COMPLETED: "Voltooid"
    }
  },

  addressForm: {
    editTitle: "Adres wijzigen",
    addTitle: "Adres toevoegen",

    addressType: "Adrestype",
    legalAddress: "Maatschappelijke zetel",
    contactAddress: "Contactadres",

    street: "Straat",
    houseNumber: "Nummer",
    box: "Bus",
    postalCode: "Postcode",
    city: "Stad",
    country: "Land",

    saving: "Opslaan...",
    save: "Opslaan",
    cancel: "Annuleren"
  },

  addressList: {
    title: "Mijn adressen",
    subtitle: "Beheer de wettelijke en contactadressen van uw onderneming.",

    add: "Adres toevoegen",
    loading: "Adressen laden...",

    legalAddress: "Maatschappelijke zetel",
    contactAddress: "Contactadres",
    box: "bus",

    edit: "Wijzigen",
    deleting: "Verwijderen...",
    delete: "Verwijderen"
  },

  companyForm: {
    title: "Algemene informatie",
    subtitle: "Professionele contactgegevens en officiële informatie.",

    edit: "Wijzigen",

    name: "Naam",
    phone: "Telefoon",
    email: "Professioneel e-mailadres",
    website: "Website",
    enterpriseNumber: "Ondernemingsnummer",
    vatNumber: "Btw-nummer",
    description: "Beschrijving",

    notProvided: "Niet ingevuld",

    editTitle: "Informatie wijzigen",
    companyName: "Naam van de onderneming",

    readonlyHelp: "Het ondernemingsnummer en het btw-nummer kunnen hier niet worden gewijzigd.",

    saving: "Opslaan...",
    save: "Opslaan",
    cancel: "Annuleren"
  },

  locationForm: {
    editTitle: "Locatie wijzigen",
    addTitle: "Locatie toevoegen",

    name: "Naam van de locatie",
    street: "Straat",
    houseNumber: "Nummer",
    box: "Bus",
    postalCode: "Postcode",
    city: "Stad",
    country: "Land",

    gpsHint: "GPS-coördinaten (optioneel) — klik in Google Maps met de rechtermuisknop op de locatie: de coördinaten worden automatisch gekopieerd.",

    latitude: "Breedtegraad",
    longitude: "Lengtegraad",
    accessInformation: "Toegangsinformatie",

    saving: "Opslaan...",
    save: "Opslaan",
    cancel: "Annuleren"
  },

  locations: {
    title: "Mijn locaties",
    subtitle: "Locaties waar uw activiteiten plaatsvinden. Een locatie kan voor meerdere tijdsloten worden hergebruikt.",

    add: "Locatie toevoegen",
    loading: "Locaties laden...",

    name: "Naam",
    address: "Adres",
    city: "Stad",
    coordinates: "Coördinaten",
    access: "Toegang",
    actions: "Acties",

    notProvided: "Niet ingevuld",
    edit: "Wijzigen",

    empty: "Geen locatie geregistreerd.",

    loadError: "De locaties konden niet worden geladen.",
    updateSuccess: "Locatie succesvol gewijzigd.",
    addSuccess: "Locatie succesvol toegevoegd.",
    saveError: "De locatie kon niet worden opgeslagen.",

    boxShort: "bus"
  },

  logoForm: {
    logoAlt: "Logo van de onderneming",
    uploading: "Uploaden...",
    editLogo: "Logo wijzigen"
  },

  page: {
    title: "Mijn onderneming",
    subtitle: "Beheer de professionele informatie van uw onderneming.",

    loading: "Laden...",
    logoTitle: "Logo van de onderneming",

    companyLoadError: "De informatie van de onderneming kon niet worden geladen.",

    loadError: "De informatie kon niet worden geladen.",
    addressesLoadError: "De adressen konden niet worden geladen.",

    profileUpdateSuccess: "Informatie succesvol gewijzigd.",
    profileUpdateError: "De informatie kon niet worden gewijzigd.",

    logoUpdateSuccess: "Logo succesvol gewijzigd.",
    logoUpdateError: "Het logo kon niet worden gewijzigd.",

    addressUpdateSuccess: "Adres succesvol gewijzigd.",
    addressAddSuccess: "Adres succesvol toegevoegd.",
    addressSaveError: "Het adres kon niet worden opgeslagen.",

    confirmAddressDelete: "Dit adres verwijderen?",
    addressDeleteSuccess: "Adres succesvol verwijderd.",
    addressDeleteError: "Het adres kon niet worden verwijderd."
  }
};