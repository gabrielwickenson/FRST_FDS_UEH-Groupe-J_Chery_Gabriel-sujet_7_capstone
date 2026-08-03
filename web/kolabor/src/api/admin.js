import api from "./client.js";

// GET /api/admin/kpis — Indicateurs clés de la plateforme (comptes, réservations, revenus, litiges)
export function getKpis() {
  return api.get("/admin/kpis").then((r) => r.data);
}

// GET /api/admin/revenus-mensuels — Revenus des 6 derniers mois, toute la plateforme
export function getRevenusMensuels() {
  return api.get("/admin/revenus-mensuels").then((r) => r.data);
}

// GET /api/admin/top-categories — Répartition des revenus par catégorie de service
export function getTopCategories() {
  return api.get("/admin/top-categories").then((r) => r.data);
}

// GET /api/admin/users — Liste de tous les utilisateurs (clients, prestataires, admins)
export function getUsers() {
  return api.get("/admin/users").then((r) => r.data);
}

// PUT /api/admin/prestataires/{id}/statut?statut=ACTIF|SUSPENDU — Valider (réactiver) ou suspendre un compte prestataire
export function updateStatutPrestataire(id, statut) {
  return api.put(`/admin/prestataires/${id}/statut`, null, { params: { statut } }).then((r) => r.data);
}
