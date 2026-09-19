export default {
  home: {
    subtitle: "Belgian leisure platform",
    title: "Book and shop for leisure in just a few clicks",
    description:
      "Discover activities, book your sessions and find leisure-related products from one platform.",

    searchPlaceholder: "Search for an activity or product",
    searchButton: "Search",
    noSearchResults: "No results found.",

    activitiesButton: "View activities",
    shopButton: "View shop",

    featured: "Discover",
    from: "From",
    book: "Book",
    discover: "Discover",
    previous: "Previous",
    next: "Next",
    slide: "Slide",

    activity: "Activity",
    product: "Product",

    selection: "Bel'Loisirs",
    suggestionsTitle: "Current suggestions",
    viewAll: "View all activities",
    view: "View",

    loading: "Loading suggestions...",
    emptyTitle: "More suggestions coming soon",
    emptyText:
      "Browse the activities and shop to discover the available offers.",

    categories: {
      wellness: "Wellness",
      sport: "Sport",
      bars: "Bars",
      culture: "Culture",
      shop: "Shop"
    }
  },

  activityList: {
    title: "Activities and experiences",
    subtitle:
      "Discover and compare the activities available on the platform.",

    loading: "Loading activities...",
    details: "View",
    empty: "No activities match your criteria.",
    loadError: "Unable to load activities.",

    results: "Activity results",
    resultsCount: "{count} activity/activities available",

    filters: {
      title: "Filters",
      reset: "Reset",
      all: "All",

      type: "Activity type",
      location: "City / area",
      partner: "Partner",
      price: "Price",

      minPrice: "Min.",
      maxPrice: "Max.",

      availableOnly: "Available only"
    }
  },

  activityDetail: {
    back: "Back to activities",
    loading: "Loading activity...",

    price: "Price",
    duration: "Duration",
    minutes: "minutes",
    partner: "Partner",
    partnerId: "Partner ID",

    sessionsTitle: "Available sessions",
    sessionsLoading: "Loading sessions...",
    sessionsEmpty:
      "No sessions are currently available for this activity.",

    remainingSeatsPlural: "places remaining",
    remainingSeatsSingular: "place remaining",

    quantity: "Quantity",
    redirecting: "Redirecting...",
    pay: "Pay",
    cancel: "Cancel",
    book: "Book",
    bookingUntil: "Booking available until",

    full: "Full",
    closedOn: "Reservations closed on {date}",

    invalidQuantity: "Invalid quantity.",
    bookingError: "Unable to create the reservation.",
    loadError: "Unable to load the activity details."
  },

  privacy: {
    title: "Privacy policy",
    subtitle:
      "This page explains how Bel'Loisirs collects and processes your personal data in accordance with the General Data Protection Regulation (GDPR).",

    controllerTitle: "1. Data controller",
    controllerText:
      "Bel'Loisirs is an application developed as a bachelor's final-year project in business information technology at the Institut des Carrières Commerciales (ICC), Brussels. Contact: contact{'@'}belloisirs.example (fictitious address — final-year project).",

    dataTitle: "2. Data collected",
    dataIntro:
      "When an account is created and the platform is used, the following data is collected:",

    dataIdentity: "Identity: last name, first name",
    dataContact: "Contact details: email address, phone number (optional, via the profile)",
    dataPassword:
      "Password, stored in hashed form (BCrypt), never in plain text nor in a reversible way",
    dataProfilePicture: "Profile picture (optional)",
    dataHistory:
      "History of orders and reservations made on the platform, with the associated billing data",
    dataPartners:
      "For partners: company name, professional address(es), logo",
    dataSecurityLog:
      "Security log: IP address, email address and type of action (login, registration, reservation, payment)",

    paymentData:
      "Payment data (bank card number, etc.) is never stored by Bel'Loisirs: it is processed directly by our payment provider, Stripe.",

    purposesTitle: "3. Purposes of processing",
    purposeAccount: "User account management and authentication",
    purposeReservations: "Booking activities and purchasing products",
    purposePayments: "Payment processing (via Stripe)",
    purposeSecurity:
      "Platform security and traceability of sensitive operations (audit log)",

    legalBasisTitle: "4. Legal basis",
    legalBasisText:
      "Processing is based on the performance of the contract for the management of the account, reservations and purchases; on a legal obligation for the retention of billing data; and on the legitimate interest of Bel'Loisirs for the security log (prevention of fraud and abusive access).",

    retentionTitle: "5. Retention period",
    retentionText:
      "Account data is kept until the account is deleted; it is then pseudonymised. Billing data is kept for 10 years, in accordance with Belgian accounting obligations. The security log is kept for 12 months.",

    recipientsTitle: "6. Data recipients",
    recipientsText:
      "Your data is shared only with the service providers required for the operation of the platform: Stripe (payment) and Railway (hosting).",

    rightsTitle: "7. Your rights",
    rightsText:
      "Under the GDPR, you have the right of access, rectification, erasure, restriction and portability of your data, as well as the right to object to processing based on legitimate interest.",
    rightsContact:
      "To exercise these rights, contact us at the following address: contact{'@'}belloisirs.example (fictitious address — final-year project).",
    complaint:
      "You also have the right to lodge a complaint with the Belgian Data Protection Authority (DPA).",

    securityTitle: "8. Security",
    securityText:
      "Passwords are hashed (BCrypt) and authentication relies on time-limited JWT tokens. No bank card data passes through our servers.",

    cookiesTitle: "9. Cookies and local storage",
    cookiesText:
      "Bel'Loisirs uses no cookies, no audience measurement tools and no advertising trackers, and fonts are hosted on our own servers. The browser only keeps, in its local storage, the information strictly necessary for the service you request: the selected language, the contents of the cart and your login information. As this storage is essential to the operation of the platform, it does not require consent. Login information is erased when you log out; everything can be deleted at any time through your browser settings.",

    lastUpdate: "Last updated: 19 September 2026"
  },

  productDetail: {
    back: "Back to the shop",
    loading: "Loading product...",

    price: "Price",
    stock: "Stock",
    available: "available",
    status: "Status",

    availableStatus: "Available",
    unavailableStatus: "Unavailable",

    addToCart: "Add to cart",

    loadError: "Unable to load the product details.",
    addedToCart: "Product added to cart!"
  },

  shop: {
    title: "Shop",
    subtitle:
      "Discover the products available for your leisure activities.",

    loading: "Loading products...",
    stock: "Stock",
    details: "View details",

    empty: "No products available.",
    loadError: "Unable to load products."
  }
};