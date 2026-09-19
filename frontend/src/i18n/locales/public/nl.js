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
      "Bel'Loisirs is een applicatie die werd ontwikkeld in het kader van een eindwerk toegepaste informatica aan het Institut des Carrières Commerciales (ICC), Brussel. Contact: contact{'@'}belloisirs.example (fictief adres — eindwerk).",

    dataTitle: "2. Verzamelde gegevens",
    dataIntro:
      "Bij het aanmaken van een account en het gebruik van het platform worden de volgende gegevens verzameld:",

    dataIdentity: "Identiteit: naam, voornaam",
    dataContact: "Contactgegevens: e-mailadres, telefoonnummer (optioneel, via het profiel)",
    dataPassword:
      "Wachtwoord, gehasht opgeslagen (BCrypt), nooit als leesbare tekst en nooit omkeerbaar",
    dataProfilePicture: "Profielfoto (optioneel)",
    dataHistory:
      "Geschiedenis van bestellingen en reservaties die via het platform zijn uitgevoerd, met de bijbehorende factuurgegevens",
    dataPartners:
      "Voor partners: naam van de onderneming, professioneel adres of professionele adressen, logo",
    dataSecurityLog:
      "Beveiligingslogboek: IP-adres, e-mailadres en type handeling (aanmelding, registratie, reservatie, betaling)",

    paymentData:
      "Betalingsgegevens (bankkaartnummer enz.) worden nooit door Bel'Loisirs opgeslagen: ze worden rechtstreeks verwerkt door onze betalingsdienstverlener Stripe.",

    purposesTitle: "3. Doeleinden van de verwerking",
    purposeAccount: "Beheer van het gebruikersaccount en authenticatie",
    purposeReservations:
      "Reservatie van activiteiten en aankoop van producten",
    purposePayments: "Verwerking van betalingen (via Stripe)",
    purposeSecurity:
      "Beveiliging van het platform en traceerbaarheid van gevoelige handelingen (auditlogboek)",

    legalBasisTitle: "4. Rechtsgrond",
    legalBasisText:
      "De verwerking is gebaseerd op de uitvoering van de overeenkomst voor het beheer van het account, de reservaties en de aankopen; op een wettelijke verplichting voor de bewaring van de factuurgegevens; en op het gerechtvaardigd belang van Bel'Loisirs voor het beveiligingslogboek (voorkoming van fraude en misbruik van toegang).",

    retentionTitle: "5. Bewaartermijn",
    retentionText:
      "De accountgegevens worden bewaard tot het account wordt verwijderd; ze worden dan gepseudonimiseerd. De factuurgegevens worden 10 jaar bewaard, overeenkomstig de Belgische boekhoudkundige verplichtingen. Het beveiligingslogboek wordt 12 maanden bewaard.",

    recipientsTitle: "6. Ontvangers van de gegevens",
    recipientsText:
      "Uw gegevens worden uitsluitend gedeeld met dienstverleners die noodzakelijk zijn voor de werking van het platform: Stripe (betalingen) en Railway (hosting).",

    rightsTitle: "7. Uw rechten",
    rightsText:
      "Overeenkomstig de AVG beschikt u over een recht op inzage, rectificatie, wissing, beperking en overdraagbaarheid van uw gegevens, evenals een recht van bezwaar tegen de verwerking op basis van het gerechtvaardigd belang.",
    rightsContact:
      "Om deze rechten uit te oefenen, kunt u contact met ons opnemen via het volgende adres: contact{'@'}belloisirs.example (fictief adres — eindwerk).",
    complaint:
      "U hebt eveneens het recht om een klacht in te dienen bij de Gegevensbeschermingsautoriteit (GBA) in België.",

    securityTitle: "8. Beveiliging",
    securityText:
      "Wachtwoorden worden gehasht met BCrypt en de authenticatie maakt gebruik van JWT-tokens met een beperkte geldigheidsduur. Er worden geen bankkaartgegevens via onze servers verwerkt.",

    cookiesTitle: "9. Cookies en lokale opslag",
    cookiesText:
      "Bel'Loisirs gebruikt geen cookies, geen tools voor bezoekersstatistieken en geen advertentietrackers, en de lettertypen worden op onze eigen servers gehost. De browser bewaart in zijn lokale opslag enkel de informatie die strikt noodzakelijk is voor de dienst die u vraagt: de gekozen taal, de inhoud van het winkelmandje en uw aanmeldingsgegevens. Aangezien deze opslag onmisbaar is voor de werking van het platform, is er geen toestemming voor vereist. De aanmeldingsgegevens worden bij het afmelden gewist; alles kan op elk moment worden verwijderd via de instellingen van uw browser.",

    lastUpdate: "Laatste update: 19 september 2026"
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