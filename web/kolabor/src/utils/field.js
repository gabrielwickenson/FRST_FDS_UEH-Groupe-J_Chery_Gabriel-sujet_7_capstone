// Petit utilitaire de lecture "tolérante" des réponses API.
// Le backend documente des propriétés accentuées (ex. "e-mail", "rôle",
// "compétences"). Certaines implémentations renvoient parfois des variantes
// sans accents (ex. "email", "role"). `field` essaie chaque clé dans l'ordre
// et renvoie la première valeur définie, pour éviter que l'UI casse si la
// forme exacte varie légèrement.
export function field(obj, ...keys) {
  if (!obj) return undefined;
  for (const k of keys) {
    if (obj[k] !== undefined && obj[k] !== null) return obj[k];
  }
  return undefined;
}

export function getUserId(u) {
  return field(u, "identifiant", "id");
}

export function getUserRole(u) {
  return field(u, "rôle", "role");
}

export function getUserName(u) {
  return field(u, "nom", "name");
}

export function getUserEmail(u) {
  return field(u, "e-mail", "email");
}

// Normalise une réponse de liste, quelle que soit l'enveloppe utilisée par
// le backend : tableau brut, page Spring (`{content:[...]}`), ou enveloppe
// générique (`{data:[...]}`, `{items:[...]}`, `{results:[...]}`). Sans ça,
// une réponse paginée (très courante côté Spring Boot) est silencieusement
// traitée comme vide par `Array.isArray(x) ? x : []`, ce qui viderait par
// exemple la liste des catégories/métiers dans les menus déroulants.
export function toArray(data) {
  if (Array.isArray(data)) return data;
  if (!data || typeof data !== "object") return [];
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  if (Array.isArray(data.items)) return data.items;
  if (Array.isArray(data.results)) return data.results;
  return [];
}
