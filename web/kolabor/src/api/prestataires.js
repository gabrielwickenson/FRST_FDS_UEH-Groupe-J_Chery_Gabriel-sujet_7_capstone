import api from "./client.js";

// GET /api/prestataires/recherche — Recherche publique de prestataires (filtres optionnels)
export function rechercherPrestataires(params) {
  return api.get("/prestataires/recherche", { params }).then((r) => r.data);
}

// PUT /api/prestataires/{id}/availability — Mettre à jour la disponibilité du prestataire
export function updateAvailability(id, disponible) {
  return api.put(`/prestataires/${id}/availability`, { disponible }).then((r) => r.data);
}

// PUT /api/prestataires/{id} — Mettre à jour le profil du prestataire (tarif horaire, compétences, zone, etc.)
export function updateProfile(id, payload) {
  return api.put(`/prestataires/${id}`, payload).then((r) => r.data);
}

// GET /api/prestataires/{id}/disponibilites — Lister les disponibilités d'un prestataire
export function getDisponibilites(id) {
  return api.get(`/prestataires/${id}/disponibilites`).then((r) => r.data);
}

// POST /api/prestataires/{id}/disponibilites — Ajouter une disponibilité pour un prestataire
export function addDisponibilite(id, disponibilite) {
  return api.post(`/prestataires/${id}/disponibilites`, disponibilite).then((r) => r.data);
}

// DELETE /api/prestataires/disponibilites/{disponibiliteId} — Supprimer une disponibilité
export function deleteDisponibilite(disponibiliteId) {
  return api.delete(`/prestataires/disponibilites/${disponibiliteId}`).then((r) => r.data);
}

// GET /api/prestataires/{id}/statistiques — Statistiques d'un prestataire (revenus, nombre prestations, note moyenne)
export function getStatistiques(id) {
  return api.get(`/prestataires/${id}/statistiques`).then((r) => r.data);
}

// GET /api/prestataires/{id}/revenue/week — Revenus des 7 derniers jours pour un prestataire
export function getRevenueWeek(id) {
  return api.get(`/prestataires/${id}/revenue/week`).then((r) => r.data);
}
