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

    // Changement de mot de passe
    changePasswordTitle: "Mijn wachtwoord wijzigen",
    changePasswordText: "Na de wijziging wordt u afgemeld en moet u zich opnieuw aanmelden met uw nieuwe wachtwoord.",
    currentPassword: "Huidig wachtwoord",
    newPassword: "Nieuw wachtwoord",
    confirmNewPassword: "Bevestig het nieuwe wachtwoord",
    passwordRule: "Minstens 12 tekens, met een hoofdletter, een kleine letter, een cijfer en een speciaal teken.",
    changePassword: "Wachtwoord wijzigen",
    changingPassword: "Bezig met wijzigen...",
    passwordTooWeak: "Het nieuwe wachtwoord voldoet niet aan de beveiligingsregels.",
    passwordMismatch: "De twee nieuwe wachtwoorden komen niet overeen.",
    passwordSameAsCurrent: "Het nieuwe wachtwoord moet verschillen van het huidige wachtwoord.",
    currentPasswordIncorrect: "Het huidige wachtwoord is onjuist.",
    changePasswordError: "Het wachtwoord kon niet worden gewijzigd. Probeer het later opnieuw.",

    rolesLabels: {
      MEMBER: "Lid",
      PARTNER: "Partner",
      ADMIN: "Beheerder"
    }
  },

  reservations: {
    title: "Mijn aankopen",
    subtitle: "Bekijk uw gereserveerde activiteiten en uw productbestellingen.",

    reservationsTitle: "Gereserveerde activiteiten",
    ordersTitle: "Bestelde producten",
    ordersLoading: "Bestellingen laden...",
    orderNumber: "Bestelling",
    orderDate: "Datum",
    items: "Artikelen",
    paidAt: "Betaald op",
    ordersEmpty: "Geen bestelling gevonden.",
    ordersLoadError: "Uw bestellingen konden niet worden geladen.",

    orderStatuses: {
      PENDING: "In afwachting",
      PAID: "Betaald",
      CANCELLED: "Geannuleerd",
      SHIPPED: "Verzonden"
    },

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
    cancelConfirmation: "Wilt u reservatie {reference} echt annuleren? Het betaalde bedrag wordt volledig terugbetaald.",
    cancelSuccess: "Reservatie {reference} is geannuleerd. De terugbetaling verschijnt binnen enkele dagen op uw betaalmiddel.",
    cancelError: "Deze reservatie kon niet worden geannuleerd.",
    refundNotAvailable: "Deze reservatie kan niet automatisch worden terugbetaald. Neem contact met ons op via contact{'@'}belloisirs.example om ze te annuleren.",
    cancelOnRequest: "Annulering op aanvraag",

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