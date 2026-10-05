export const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{12,}$/;

export function isStrongPassword(password) {
  return PASSWORD_PATTERN.test(password || "");
}