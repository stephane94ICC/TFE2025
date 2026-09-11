export default {
  success: {
    title: "Payment successful",

    reservationMessage:
      "The Stripe test payment was accepted. Your reservation has been registered on the platform.",

    orderMessage:
      "The Stripe test payment was accepted. Your order has been created on the platform.",

    stripeSession: "Stripe session",

    backActivities: "Back to activities",
    backShop: "Back to the shop",
    backHome: "Back to home"
  },

  cancel: {
    title: "Payment cancelled",

    reservationMessage:
      "The Stripe payment was cancelled. Your reservation was not confirmed and the places have been released.",

    orderMessage:
      "The Stripe payment was cancelled. Your cart has been kept so that you can try again.",

    reservationCancelSuccess:
      "The reservation linked to this payment has been cancelled.",

    reservationCancelError:
      "The payment was cancelled, but the reservation could not be updated automatically.",

    orderCancelSuccess:
      "The order linked to this payment has been cancelled.",

    orderCancelError:
      "The payment was cancelled, but the order could not be updated automatically.",

    backActivities: "Back to activities",
    backCart: "Back to cart",
    backHome: "Back to home"
  }
};