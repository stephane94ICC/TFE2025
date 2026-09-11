export default {
  activities: {
    title: "My activities",
    subtitle: "View your activities and their validation status.",
    add: "Add an activity",
    loading: "Loading activities...",

    activityTitle: "Title",
    price: "Price",
    duration: "Duration",
    status: "Status",
    actions: "Actions",

    edit: "Edit",
    manageImages: "Manage images",
    manageSessions: "Manage sessions",
    rejectionReason: "Reason for rejection",

    empty: "No activities found.",
    loadError: "Unable to load activities.",

    statuses: {
      PENDING_REVIEW: "Pending",
      APPROVED: "Approved",
      REJECTED: "Rejected",
      DISABLED: "Disabled"
    }
  },

  activityForm: {
    editTitle: "Edit activity",
    addTitle: "Add activity",

    note: "After it is saved, the activity must be approved by the administrator before it becomes publicly visible.",

    title: "Title",
    description: "Description",
    price: "Price (€)",
    duration: "Duration (minutes)",
    minimumAge: "Minimum age",
    equipmentInformation: "Equipment information",

    saving: "Saving...",
    save: "Save",
    cancel: "Cancel",

    saveError: "Unable to save the activity."
  },

  activityImages: {
    title: "Images",
    loading: "Loading...",

    uploading: "Uploading...",
    addImage: "Add an image",

    imageAlt: "Activity image",
    defaultImage: "Default image",

    deleting: "Deleting...",
    delete: "Delete",

    empty: "No images for this activity.",

    loadError: "Unable to load images.",
    addSuccess: "Image added successfully.",
    addError: "Unable to add the image.",
    deleteSuccess: "Image deleted successfully.",
    deleteError: "Unable to delete the image."
  },

  activityRejection: {
    title: "Reason for rejection",
    noComment: "No comment was provided by the administrator.",
    close: "Close"
  },

  activitySessions: {
    title: "Sessions",
    loading: "Loading...",

    addTitle: "Add a session",
    noLocation: "You must first register a location in “My locations”.",

    location: "Location",
    chooseLocation: "-- Choose a location --",
    capacity: "Capacity",
    start: "Start",
    end: "End",
    bookingDeadline: "Reservation deadline",

    deadline2Hours: "2 hours before the start",
    deadline24Hours: "24 hours before the start",
    deadline48Hours: "48 hours before the start",
    deadline1Week: "1 week before the start",

    saving: "Saving...",
    add: "Add session",

    seats: "Places",
    deadline: "Deadline",
    status: "Status",

    empty: "No sessions for this activity.",

    loadError: "Unable to load sessions.",
    endBeforeStart: "The end date must be after the start date.",
    addSuccess: "Session added successfully.",
    addError: "Unable to add the session.",

    statuses: {
      SCHEDULED: "Scheduled",
      CANCELLED: "Cancelled",
      COMPLETED: "Completed"
    }
  },

  addressForm: {
    editTitle: "Edit address",
    addTitle: "Add address",

    addressType: "Address type",
    legalAddress: "Registered office",
    contactAddress: "Contact address",

    street: "Street",
    houseNumber: "Number",
    box: "Box",
    postalCode: "Postal code",
    city: "City",
    country: "Country",

    saving: "Saving...",
    save: "Save",
    cancel: "Cancel"
  },

  addressList: {
    title: "My addresses",
    subtitle: "Manage your company's registered and contact addresses.",

    add: "Add an address",
    loading: "Loading addresses...",

    legalAddress: "Registered office",
    contactAddress: "Contact address",
    box: "box",

    edit: "Edit",
    deleting: "Deleting...",
    delete: "Delete"
  },

  companyForm: {
    title: "General information",
    subtitle: "Professional contact details and official information.",

    edit: "Edit",

    name: "Name",
    phone: "Phone",
    email: "Professional email",
    website: "Website",
    enterpriseNumber: "Enterprise number",
    vatNumber: "VAT number",
    description: "Description",

    notProvided: "Not provided",

    editTitle: "Edit information",
    companyName: "Company name",

    readonlyHelp: "The enterprise number and VAT number cannot be changed here.",

    saving: "Saving...",
    save: "Save",
    cancel: "Cancel"
  },

  locationForm: {
    editTitle: "Edit location",
    addTitle: "Add location",

    name: "Location name",
    street: "Street",
    houseNumber: "Number",
    box: "Box",
    postalCode: "Postal code",
    city: "City",
    country: "Country",

    gpsHint: "GPS coordinates (optional) — in Google Maps, right-click the location: the coordinates are copied automatically.",

    latitude: "Latitude",
    longitude: "Longitude",
    accessInformation: "Access information",

    saving: "Saving...",
    save: "Save",
    cancel: "Cancel"
  },

  locations: {
    title: "My locations",
    subtitle: "Locations where your activities take place. A location can be reused for several sessions.",

    add: "Add a location",
    loading: "Loading locations...",

    name: "Name",
    address: "Address",
    city: "City",
    coordinates: "Coordinates",
    access: "Access",
    actions: "Actions",

    notProvided: "Not provided",
    edit: "Edit",

    empty: "No location registered.",

    loadError: "Unable to load locations.",
    updateSuccess: "Location updated successfully.",
    addSuccess: "Location added successfully.",
    saveError: "Unable to save the location.",

    boxShort: "box"
  },

  logoForm: {
    logoAlt: "Company logo",
    uploading: "Uploading...",
    editLogo: "Edit logo"
  },

  page: {
    title: "My company",
    subtitle: "Manage your company's professional information.",

    loading: "Loading...",
    logoTitle: "Company logo",

    companyLoadError: "Unable to load company information.",

    loadError: "Unable to load information.",
    addressesLoadError: "Unable to load addresses.",

    profileUpdateSuccess: "Information updated successfully.",
    profileUpdateError: "Unable to update information.",

    logoUpdateSuccess: "Logo updated successfully.",
    logoUpdateError: "Unable to update the logo.",

    addressUpdateSuccess: "Address updated successfully.",
    addressAddSuccess: "Address added successfully.",
    addressSaveError: "Unable to save the address.",

    confirmAddressDelete: "Delete this address?",
    addressDeleteSuccess: "Address deleted successfully.",
    addressDeleteError: "Unable to delete the address."
  }
};