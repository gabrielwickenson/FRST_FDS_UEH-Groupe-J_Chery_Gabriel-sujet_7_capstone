import api from "./client.js";

// POST /api/reservations — Créer une nouvelle réservation (client)
export function createReservation(payload) {
  return api.post("/reservations", payload).then((r) => r.data);
}

// PUT /api/reservations/{id}/statut?statut=...&prestataireId=... — Mettre à jour le statut (prestataire)
// Le backend attend `statut` et `prestataireId` en paramètres de requête, pas dans le corps JSON.
export function updateStatut(id, statut, prestataireId) {
  return api.put(`/reservations/${id}/statut`, null, { params: { statut, prestataireId } }).then((r) => r.data);
}

// PUT /api/reservations/{id}/annuler?clientId=... — Annuler sa propre réservation (client)
// Distinct de updateStatut ci-dessus, qui est réservé au prestataire (il
// vérifie prestataireId, jamais clientId) et ne peut donc pas servir à un
// client pour annuler sa propre réservation.
export function annulerReservationClient(id, clientId) {
  return api.put(`/reservations/${id}/annuler`, null, { params: { clientId } }).then((r) => r.data);
}

// POST /api/reservations/{id}/paiement?modePaiement=...&clientId=... — Simuler un paiement (client)
// Le backend attend ces deux valeurs en paramètres de requête, pas dans le corps JSON.
export function payerReservation(id, modePaiement, clientId) {
  return api.post(`/reservations/${id}/paiement`, null, { params: { modePaiement, clientId } }).then((r) => r.data);
}

// POST /api/reservations/{id}/litige?clientId=... — Ouvrir un litige (client)
// `motif` va dans le corps JSON, mais `clientId` doit être un paramètre de requête.
export function ouvrirLitige(id, motif, clientId) {
  return api.post(`/reservations/${id}/litige`, { motif }, { params: { clientId } }).then((r) => r.data);
}

// PUT /api/reservations/litiges/{litigeId}?resolution=... — Résoudre un litige (admin)
// Le backend attend `resolution` en paramètre de requête ; il n'y a pas de corps JSON.
export function resoudreLitige(litigeId, resolution) {
  return api.put(`/reservations/litiges/${litigeId}`, null, { params: { resolution } }).then((r) => r.data);
}

// GET /api/reservations/litiges/ouverts — Consulter tous les litiges ouverts (admin)
export function getLitigesOuverts() {
  return api.get("/reservations/litiges/ouverts").then((r) => r.data);
}

// GET /api/reservations/{id}/avis — Récupérer tous les avis d'un prestataire
export function getAvis(id) {
  return api.get(`/reservations/${id}/avis`).then((r) => r.data);
}

// POST /api/reservations/{id}/avis?clientId=... — Laisser un avis sur une réservation (client)
// Le backend attend `clientId` en paramètre de requête en plus du corps JSON {note, commentaire}.
export function laisserAvis(id, payload, clientId) {
  return api.post(`/reservations/${id}/avis`, payload, { params: { clientId } }).then((r) => r.data);
}

// GET /api/reservations/prestataire/{prestataireId} — Réservations d'un prestataire (agenda)
export function getReservationsPrestataire(prestataireId) {
  return api.get(`/reservations/prestataire/${prestataireId}`).then((r) => r.data);
}

// GET /api/reservations/me/prestataire — Agenda du prestataire connecté
export function getMesReservationsPrestataire() {
  return api.get("/reservations/me/prestataire").then((r) => r.data);
}

// GET /api/reservations/me/client — Réservations du client connecté
// (le backend expose /me/client, pas /moi/client — l'ancien chemin causait
// un 404 systématique sur "Mes réservations" côté client).
export function getMesReservationsClient() {
  return api.get("/reservations/me/client").then((r) => r.data);
}

// GET /api/reservations/client/{clientId} — Historique des réservations d'un client
export function getReservationsClient(clientId) {
  return api.get(`/reservations/client/${clientId}`).then((r) => r.data);
}
