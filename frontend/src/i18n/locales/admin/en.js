export default {
  dashboard: {
    title: "Administrator area",
    introduction: "Welcome to the administrator dashboard. This page provides access to the main management areas of the platform.",

    usersTitle: "Users",
    usersDescription: "View and manage user accounts registered on the platform.",

    activitiesTitle: "Activities",
    activitiesDescription: "Manage the activities offered by partners.",

    productsTitle: "Products",
    productsDescription: "Manage the products available in the shop.",

    activityLogTitle: "Activity log",
    activityLogDescription: "View the history of logins and actions performed on the platform.",

    partnersTitle: "Partners",
    partnersDescription: "View and manage the platform's partners.",

    access: "Open",
    comingSoon: "Coming soon"
  },

  activities: {
    title: "Activity management",
    subtitle: "View activities and validate proposals submitted by partners.",
    add: "Add an activity",
    loading: "Loading activities...",

    id: "ID",
    activityTitle: "Title",
    price: "Price",
    partner: "Partner",
    status: "Status",
    actions: "Actions",

    partnerFallback: "Partner #{id}",

    edit: "Edit",
    images: "Images",
    approve: "Approve",
    reject: "Reject",
    disable: "Disable",

    approveAction: "approve",
    disableAction: "disable",
    confirmReview: "Are you sure you want to {action} this activity?",

    empty: "No activity found.",

    loadError: "Unable to load activities.",
    updateSuccess: "The activity status has been updated.",
    updateError: "Unable to update the activity status.",

    statuses: {
      PENDING_REVIEW: "Pending",
      APPROVED: "Approved",
      REJECTED: "Rejected",
      DISABLED: "Disabled"
    }
  },

  activityForm: {
    editTitle: "Edit an activity",
    addTitle: "Add an activity",

    title: "Title",
    description: "Description",
    price: "Price",
    city: "City",
    partnerId: "Partner ID",

    cancel: "Cancel",
    save: "Save",

    loadError: "Unable to load the activity.",
    saveError: "Unable to save the activity."
  },

  activityImages: {
    title: "Activity images",
    loading: "Loading...",
    activityLabel: "Activity:",

    uploading: "Uploading...",
    addImage: "Add an image",

    imageAlt: "Activity image",
    defaultImage: "Default image",

    deleting: "Deleting...",
    delete: "Delete",

    backToList: "Back to the list",

    loadError: "Unable to load images.",
    addSuccess: "Image added successfully.",
    addError: "Unable to add the image.",
    deleteSuccess: "Image deleted successfully.",
    deleteError: "Unable to delete the image."
  },

  activityReview: {
    title: "Reject activity",
    instruction: "Indicate the reason for the rejection. The partner will be able to view this comment.",
    placeholder: "Reason for rejection...",
    confirm: "Confirm rejection",
    cancel: "Cancel",
    requiredError: "The reason for rejection is required."
  },

  activityLogs: {
    title: "Activity log",
    subtitle: "History of events recorded on the platform.",
    resetFilters: "Reset filters",

    filters: "Filters",
    eventType: "Event type",
    allTypes: "All types",
    email: "Email address",
    partialSearch: "Partial search",
    from: "From",
    to: "To",
    search: "Search",

    events: "Events",
    entryCount: "{count} entry(ies)",
    loading: "Loading activity log...",

    date: "Date",
    event: "Event",
    user: "User",
    details: "Details",
    ipAddress: "IP address",

    empty: "No events match the criteria.",

    previous: "Previous",
    page: "Page {current} of {total}",
    next: "Next",

    loadError: "Unable to load the activity log.",

    eventTypes: {
      LOGIN_SUCCESS: "Login successful",
      LOGIN_FAILURE: "Login failed",
      REGISTER: "Registration",
      ACCESS_DENIED_INACTIVE_ACCOUNT: "Access denied — account disabled",
      PROFILE_UPDATED: "Profile updated",
      ACCOUNT_DELETION_REQUESTED: "Account deletion requested",
      ACCOUNT_ANONYMIZED: "Account anonymised",
      RESERVATION_CREATED: "Reservation created",
      RESERVATION_CONFIRMED: "Reservation confirmed",
      RESERVATION_CANCELLED: "Reservation cancelled",
      ORDER_PAID: "Order paid",
      ACTIVITY_APPROVED: "Activity approved",
      ACTIVITY_REJECTED: "Activity rejected",
      USER_MODIFIED_BY_ADMIN: "User modified by an administrator"
    }
  },

  products: {
    title: "Product management",
    description: "This page allows the administrator to view the products in the shop.",
    loading: "Loading products...",

    listTitle: "Product list",
    productCount: "{count} product(s)",
    add: "Add a product",

    id: "ID",
    name: "Name",
    price: "Price",
    stock: "Stock",
    active: "Active",
    actions: "Actions",

    yes: "Yes",
    no: "No",

    edit: "Edit",
    manageImages: "Manage images",
    deleting: "Deleting...",
    delete: "Delete",

    empty: "No product found.",

    loadError: "Unable to load products.",
    confirmDelete: "Are you sure you want to delete the product \"{name}\"?",
    deleteSuccess: "Product deleted successfully.",
    deleteError: "Unable to delete the product."
  },

  productForm: {
    editTitle: "Edit a product",
    addTitle: "Add a product",

    editDescription: "This form allows the administrator to edit an existing product.",
    addDescription: "This form allows the administrator to add a new product to the shop.",

    loading: "Loading product...",

    name: "Product name",
    namePlaceholder: "Example: Yoga mat",

    description: "Description",
    descriptionPlaceholder: "Product description",

    price: "Price",
    stock: "Stock",

    activeLabel: "Product active in the shop",

    cancel: "Cancel",
    saving: "Saving...",
    editButton: "Edit product",
    addButton: "Add product",

    loadError: "Unable to load the product.",
    editError: "Unable to edit the product.",
    addError: "Unable to add the product."
  },

  productImages: {
    title: "Product images",
    loading: "Loading...",
    productLabel: "Product:",

    uploading: "Uploading...",
    addImage: "Add an image",

    imageAlt: "Product image",
    defaultImage: "Default image",

    deleting: "Deleting...",
    delete: "Delete",

    backToList: "Back to the list",

    loadError: "Unable to load images.",
    addSuccess: "Image added successfully.",
    addError: "Unable to add the image.",
    deleteSuccess: "Image deleted successfully.",
    deleteError: "Unable to delete the image."
  },

  users: {
    title: "User management",
    subtitle: "List of users registered on the platform.",

    cancel: "Cancel",
    addUser: "Add a user",

    createTitle: "Create a user",
    editTitle: "Edit user",
    listTitle: "User list",

    id: "ID",
    email: "Email",
    firstName: "First name",
    lastName: "Last name",
    password: "Password",
    newPassword: "New password",
    role: "Role",
    rgpd: "GDPR",
    actions: "Actions",

    emailPlaceholder: "example@email.com",
    firstNamePlaceholder: "First name",
    lastNamePlaceholder: "Last name",
    passwordPlaceholder: "Password",
    unchangedPasswordPlaceholder: "Leave blank if unchanged",

    rgpdInfo: "GDPR consent is given by the user during registration.",

    create: "Create",
    save: "Save",

    loading: "Loading users...",

    yes: "Yes",
    no: "No",

    edit: "Edit",
    delete: "Delete",

    empty: "No user found.",

    loadError: "Unable to load users.",
    requiredCreate: "Please complete all fields.",
    createSuccess: "User created successfully.",
    createError: "Error while creating the user.",

    requiredEdit: "Please complete the email, first name and last name.",
    updateSuccess: "User updated successfully.",
    updateError: "Error while updating the user.",

    confirmDelete: "Are you sure you want to delete this user?",
    deleteSuccess: "User deleted successfully.",
    deleteError: "Error while deleting the user.",

    roles: {
      MEMBER: "Member",
      PARTNER: "Partner",
      ADMIN: "Administrator"
    }
  }
};