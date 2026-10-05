export function normalizeEnterpriseNumber(raw) {
  return (raw || "").replace(/[\s.-]/g, "");
}

export function isValidEnterpriseNumber(raw) {
  const normalized = normalizeEnterpriseNumber(raw);

  if (!/^[01]\d{9}$/.test(normalized)) {
    return false;
  }

  const base = Number(normalized.slice(0, 8));
  const key = Number(normalized.slice(8));
  const remainder = base % 97;

  // Reste nul : la clé vaut 97, pas 0
  return key === (remainder === 0 ? 97 : 97 - remainder);
}