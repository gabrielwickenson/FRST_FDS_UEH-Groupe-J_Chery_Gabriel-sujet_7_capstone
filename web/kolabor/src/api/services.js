import api from "./client.js";

// GET /api/services — Lister tous les services disponibles (public)
export function getServices() {
  return api.get("/services").then((r) => r.data);
}
