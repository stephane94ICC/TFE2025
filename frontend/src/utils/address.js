import i18n from "../i18n";

export function formatStreet(address) {
  const street = `${address.street} ${address.houseNumber || ""}`.trim();

  return address.box
      ? `${street} ${i18n.global.t("public.partnerShowcase.box", { box: address.box })}`
      : street;
}

export function formatCity(address) {
  return `${address.postalCode} ${address.city}`.trim();
}