export default {
  profile: {
    title: "My profile",
    subtitle: "View and edit your personal information.",

    profileImageAlt: "Profile picture",
    uploading: "Uploading...",
    editPhoto: "Change photo",

    firstName: "First name",
    lastName: "Last name",
    phone: "Phone",
    optional: "Optional",
    email: "Email",
    emailHint: "The email address is used as the login identifier and cannot be changed.",
    roles: "Role(s)",

    saving: "Saving...",
    save: "Save",
    cancel: "Cancel",

    deleteAccountTitle: "Delete my account",
    deleteAccountText: "Account deletion is permanent. Certain data may be retained when required by law.",
    deleteAccount: "Delete my account",

    noUser: "No user is logged in.",

    confirmDeleteTitle: "Confirm account deletion",
    confirmDeleteText: "Enter your password to confirm your deletion request.",
    password: "Password",
    deleting: "Deleting...",
    deletePermanently: "Delete permanently",

    loadError: "Unable to load your profile.",
    updateSuccess: "Profile updated successfully.",
    updateError: "Unable to update your profile.",
    deleteError: "Unable to delete your account.",
    photoSuccess: "Profile picture updated successfully.",
    photoError: "Unable to update the profile picture.",

    rolesLabels: {
      MEMBER: "Member",
      PARTNER: "Partner",
      ADMIN: "Administrator"
    }
  },

  reservations: {
    title: "My reservations",
    subtitle: "View your reservations and their status.",

    loading: "Loading reservations...",

    reference: "Reference",
    activity: "Activity",
    session: "Session",
    places: "Places",
    total: "Total",
    status: "Status",
    bookedAt: "Booked on",
    action: "Action",

    cancelling: "Cancelling...",
    cancel: "Cancel",
    cancellationClosed: "Cancellation closed",

    empty: "No reservations found.",

    loadError: "Unable to load your reservations.",
    cancelConfirmation: "Are you sure you want to cancel reservation {reference}?",
    cancelSuccess: "Reservation {reference} has been cancelled.",
    cancelError: "Unable to cancel this reservation.",

    statuses: {
      PENDING: "Pending",
      CONFIRMED: "Confirmed",
      CANCELLED: "Cancelled"
    }
  },

  cart: {
    title: "My cart",
    empty: "Your cart is empty.",

    quantity: "Quantity:",
    subtotal: "Subtotal:",
    delete: "Delete",

    total: "Total:",
    clear: "Clear cart",

    stripeRedirect: "Redirecting to Stripe...",
    payWithStripe: "Pay with Stripe",

    paymentError: "Unable to start the payment."
  }
};