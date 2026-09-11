export default {
  profile: {
    title: "Mijn profiel",
    subtitle: "Bekijk en wijzig uw persoonlijke gegevens.",

    profileImageAlt: "Profielfoto",
    uploading: "Uploaden...",
    editPhoto: "Foto wijzigen",

    firstName: "Voornaam",
    lastName: "Naam",
    phone: "Telefoon",
    optional: "Optioneel",
    email: "E-mail",
    emailHint: "Het e-mailadres wordt gebruikt als aanmeldingsidentificatie en kan niet worden gewijzigd.",
    roles: "Rol(len)",

    saving: "Opslaan...",
    save: "Opslaan",
    cancel: "Annuleren",

    deleteAccountTitle: "Mijn account verwijderen",
    deleteAccountText: "Het verwijderen van het account is definitief. Bepaalde gegevens kunnen worden bewaard wanneer een wettelijke verplichting dit vereist.",
    deleteAccount: "Mijn account verwijderen",

    noUser: "Geen gebruiker aangemeld.",

    confirmDeleteTitle: "Verwijdering van het account bevestigen",
    confirmDeleteText: "Voer uw wachtwoord in om uw verzoek tot verwijdering te bevestigen.",
    password: "Wachtwoord",
    deleting: "Verwijderen...",
    deletePermanently: "Definitief verwijderen",

    loadError: "Uw profiel kon niet worden geladen.",
    updateSuccess: "Profiel succesvol bijgewerkt.",
    updateError: "Uw profiel kon niet worden bijgewerkt.",
    deleteError: "Uw account kon niet worden verwijderd.",
    photoSuccess: "Profielfoto succesvol bijgewerkt.",
    photoError: "De profielfoto kon niet worden gewijzigd.",

    rolesLabels: {
      MEMBER: "Lid",
      PARTNER: "Partner",
      ADMIN: "Beheerder"
    }
  },

  reservations: {
    title: "Mijn reservaties",
    subtitle: "Bekijk uw reservaties en hun status.",

    loading: "reservaties laden...",

    reference: "Referentie",
    activity: "Activiteit",
    session: "Sessie",
    places: "Plaatsen",
    total: "Totaal",
    status: "Status",
    bookedAt: "Gereserveerd op",
    action: "Actie",

    cancelling: "Annuleren...",
    cancel: "Annuleren",
    cancellationClosed: "Annulering gesloten",

    empty: "Geen reservaties gevonden.",

    loadError: "Uw reservaties konden niet worden geladen.",
    cancelConfirmation: "Wilt u reservatie {reference} echt annuleren?",
    cancelSuccess: "Reservatie {reference} is geannuleerd.",
    cancelError: "Deze reservatie kon niet worden geannuleerd.",

    statuses: {
      PENDING: "In afwachting",
      CONFIRMED: "Bevestigd",
      CANCELLED: "Geannuleerd"
    }
  },

  cart: {
    title: "Mijn winkelmandje",
    empty: "Uw winkelmandje is leeg.",

    quantity: "Aantal:",
    subtotal: "Subtotaal:",
    delete: "Verwijderen",

    total: "Totaal:",
    clear: "Winkelmandje leegmaken",

    stripeRedirect: "Doorsturen naar Stripe...",
    payWithStripe: "Betalen met Stripe",

    paymentError: "De betaling kon niet worden gestart."
  }
};