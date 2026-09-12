export default {
  home: {
    subtitle: "Belgisch vrijetijdsplatform",
    title: "Vrije tijd reserveren en kopen in enkele klikken",
    description:
      "Ontdek activiteiten, reserveer uw tijdsloten en vind producten voor uw vrije tijd op één platform.",

    searchPlaceholder: "Zoek een activiteit of product",
    searchButton: "Zoeken",
    noSearchResults: "Geen resultaten gevonden.",

    activitiesButton: "Activiteiten bekijken",
    shopButton: "Winkel bekijken",

    featured: "Ontdek",
    from: "Vanaf",
    book: "Reserveren",
    discover: "Ontdekken",
    previous: "Vorige",
    next: "Volgende",
    slide: "Dia",

    activity: "Activiteit",
    product: "Product",

    selection: "Bel'Loisirs",
    suggestionsTitle: "Suggesties van het moment",
    viewAll: "Alle activiteiten bekijken",
    view: "Bekijken",

    loading: "Suggesties laden...",
    emptyTitle: "Binnenkort meer suggesties",
    emptyText:
      "Bekijk de activiteiten en de winkel om de beschikbare aanbiedingen te ontdekken.",

    categories: {
      wellness: "Wellness",
      sport: "Sport",
      bars: "Bars",
      culture: "Cultuur",
      shop: "Winkel"
    }
  },

  activityList: {
    title: "Activiteiten en ervaringen",
    subtitle:
      "Ontdek en vergelijk de activiteiten die beschikbaar zijn op het platform.",

    loading: "Activiteiten laden...",
    details: "Bekijken",
    empty: "Geen activiteiten voldoen aan uw criteria.",
    loadError: "De activiteiten konden niet worden geladen.",

    results: "Activiteitenresultaten",
    resultsCount: "{count} activiteit(en) beschikbaar",

    filters: {
      title: "Filters",
      reset: "Resetten",
      all: "Alle",

      type: "Type activiteit",
      location: "Stad / zone",
      partner: "Partner",
      price: "Prijs",

      minPrice: "Min.",
      maxPrice: "Max.",

      availableOnly: "Alleen beschikbaar"
    }
  },

  activityDetail: {
    back: "Terug naar de activiteiten",
    loading: "Activiteit laden...",

    price: "Prijs",
    duration: "Duur",
    minutes: "minuten",
    partner: "Partner",
    partnerId: "Partner-ID",

    sessionsTitle: "Beschikbare tijdsloten",
    sessionsLoading: "Tijdsloten laden...",
    sessionsEmpty:
      "Er zijn momenteel geen tijdsloten beschikbaar voor deze activiteit.",

    remainingSeatsPlural: "plaatsen beschikbaar",
    remainingSeatsSingular: "plaats beschikbaar",

    quantity: "Aantal",
    redirecting: "Doorsturen...",
    pay: "Betalen",
    cancel: "Annuleren",
    book: "Reserveren",
    bookingUntil: "Reserveren mogelijk tot",

    full: "Volzet",
    closedOn: "Reservaties gesloten op {date}",

    invalidQuantity: "Ongeldig aantal.",
    bookingError: "De reservatie kon niet worden aangemaakt.",
    loadError:
      "De details van de activiteit konden niet worden geladen."
  },

  privacy: {
    title: "Privacybeleid",
    subtitle:
      "Deze pagina legt uit hoe Bel'Loisirs uw persoonsgegevens verzamelt en verwerkt, overeenkomstig de Algemene Verordening Gegevensbescherming (AVG).",

    controllerTitle: "1. Verwerkingsverantwoordelijke",
    controllerText:
      "[AAN TE VULLEN: naam van de verantwoordelijke entiteit, adres, contact-e-mailadres]",

    dataTitle: "2. Verzamelde gegevens",
    dataIntro:
      "Bij het aanmaken van een account en het gebruik van het platform worden de volgende gegevens verzameld:",

    dataIdentity: "Identiteit: naam, voornaam, geboortedatum",
    dataContact: "Contactgegevens: e-mailadres, telefoonnummer",
    dataPassword:
      "Wachtwoord (versleuteld opgeslagen, nooit als leesbare tekst)",
    dataProfilePicture: "Profielfoto (optioneel)",
    dataHistory:
      "Geschiedenis van bestellingen en reservaties die via het platform zijn uitgevoerd",
    dataPartners:
      "Voor partners: naam van de onderneming, professioneel adres of professionele adressen, logo",

    paymentData:
      "Betalingsgegevens (bankkaartnummer enz.) worden nooit door Bel'Loisirs opgeslagen: ze worden rechtstreeks verwerkt door onze betalingsdienstverlener Stripe.",

    purposesTitle: "3. Doeleinden van de verwerking",
    purposeAccount: "Beheer van het gebruikersaccount en authenticatie",
    purposeReservations:
      "Reservatie van activiteiten en aankoop van producten",
    purposePayments: "Verwerking van betalingen (via Stripe)",
    purposeCommunication:
      "Communicatie over bestellingen en reservaties",

    legalBasisTitle: "4. Rechtsgrond",
    legalBasisText:
      "De verwerking is gebaseerd op de uitvoering van de overeenkomst (aanmaken van een account, aankopen, reservaties) en op uw uitdrukkelijke toestemming, die bij de registratie wordt verkregen.",

    retentionTitle: "5. Bewaartermijn",
    retentionText:
      "[AAN TE VULLEN: bewaartermijn van de gegevens na verwijdering van het account / laatste activiteit]",

    recipientsTitle: "6. Ontvangers van de gegevens",
    recipientsText:
      "Uw gegevens worden uitsluitend gedeeld met dienstverleners die noodzakelijk zijn voor de werking van het platform: Stripe (betalingen) en onze hostingprovider. [AAN TE VULLEN: naam van de hostingprovider zodra de definitieve implementatie is bevestigd]",

    rightsTitle: "7. Uw rechten",
    rightsText:
      "Overeenkomstig de AVG beschikt u over een recht op inzage, rectificatie, wissing, beperking en overdraagbaarheid van uw gegevens, evenals het recht om uw toestemming op elk moment in te trekken.",
    rightsContact:
      "Om deze rechten uit te oefenen, kunt u contact met ons opnemen via het volgende adres: [AAN TE VULLEN: contact-e-mailadres].",
    complaint:
      "U hebt eveneens het recht om een klacht in te dienen bij de Gegevensbeschermingsautoriteit (GBA) in België.",

    securityTitle: "8. Beveiliging",
    securityText:
      "Wachtwoorden worden versleuteld met BCrypt en de authenticatie maakt gebruik van JWT-tokens met een beperkte geldigheidsduur. Er worden geen bankkaartgegevens via onze servers verwerkt.",

    lastUpdate: "Laatste update: [AAN TE VULLEN]"
  },

  productDetail: {
    back: "Terug naar de winkel",
    loading: "Product laden...",

    price: "Prijs",
    stock: "Voorraad",
    available: "beschikbaar",
    status: "Status",

    availableStatus: "Beschikbaar",
    unavailableStatus: "Niet beschikbaar",

    addToCart: "Toevoegen aan winkelmandje",

    loadError: "De productdetails konden niet worden geladen.",
    addedToCart: "Product toegevoegd aan het winkelmandje!"
  },

  shop: {
    title: "Winkel",
    subtitle:
      "Ontdek de beschikbare producten voor uw vrijetijdsactiviteiten.",

    loading: "Producten laden...",
    stock: "Voorraad",
    details: "Details bekijken",

    empty: "Geen producten beschikbaar.",
    loadError: "De producten konden niet worden geladen."
  }
};