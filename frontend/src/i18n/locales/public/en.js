export default {
  home: {
    subtitle: "Belgian leisure platform",
    title: "Book and shop for leisure in just a few clicks",
    description: "Discover activities, book your sessions and find leisure-related products from one platform.",

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
    emptyText: "Browse the activities and shop to discover the available offers.",

    categories: {
      wellness: "Wellness",
      sport: "Sport",
      bars: "Bars",
      culture: "Culture",
      shop: "Shop"
    }
  },

  activityList: {
    title: "Activities",
    subtitle: "Discover the activities available on the platform.",
    loading: "Loading activities...",
    details: "View details",
    empty: "No activities available.",
    loadError: "Unable to load activities."
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
    sessionsEmpty: "No sessions are currently available for this activity.",

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
    subtitle: "This page explains how Bel'Loisirs collects and processes your personal data in accordance with the General Data Protection Regulation (GDPR).",

    controllerTitle: "1. Data controller",
    controllerText: "[TO BE COMPLETED: name of the responsible entity, address, contact email]",

    dataTitle: "2. Data collected",
    dataIntro: "When an account is created and the platform is used, the following data is collected:",

    dataIdentity: "Identity: last name, first name, date of birth",
    dataContact: "Contact details: email address, telephone number",
    dataPassword: "Password (stored in encrypted form, never in plain text)",
    dataProfilePicture: "Profile picture (optional)",
    dataHistory: "History of orders and reservations made on the platform",
    dataPartners: "For partners: company name, professional address(es), logo",

    paymentData: "Payment data (bank card number, etc.) is never stored by Bel'Loisirs: it is processed directly by our payment provider, Stripe.",

    purposesTitle: "3. Purposes of processing",
    purposeAccount: "User account management and authentication",
    purposeReservations: "Booking activities and purchasing products",
    purposePayments: "Payment processing (via Stripe)",
    purposeCommunication: "Communication relating to orders and reservations",

    legalBasisTitle: "4. Legal basis",
    legalBasisText: "Processing is based on the performance of the contract (account creation, purchases, reservations) and on your explicit consent, obtained during registration.",

    retentionTitle: "5. Retention period",
    retentionText: "[TO BE COMPLETED: data retention period after account deletion / last activity]",

    recipientsTitle: "6. Data recipients",
    recipientsText: "Your data is shared only with service providers required for the operation of the platform: Stripe (payment) and our hosting provider. [TO BE COMPLETED: name of the hosting provider once final deployment is confirmed]",

    rightsTitle: "7. Your rights",
    rightsText: "In accordance with the GDPR, you have the right to access, rectify, erase, restrict and receive your data, as well as the right to withdraw your consent at any time.",
    rightsContact: "To exercise these rights, contact us at the following address: [TO BE COMPLETED: contact email address].",
    complaint: "You also have the right to lodge a complaint with the Belgian Data Protection Authority (DPA).",

    securityTitle: "8. Security",
    securityText: "Passwords are encrypted using BCrypt and authentication is based on JWT tokens with a limited validity period. No bank card data passes through our servers.",

    lastUpdate: "Last updated: [TO BE COMPLETED]"
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
    subtitle: "Discover the products available for your leisure activities.",

    loading: "Loading products...",
    stock: "Stock",
    details: "View details",

    empty: "No products available.",
    loadError: "Unable to load products."
  }
};