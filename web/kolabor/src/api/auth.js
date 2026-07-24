import api from "./client.js";

// POST /api/auth/register — Inscription d'un nouvel utilisateur (client ou prestataire)
export function register(payload) {
  return api.post("/auth/register", payload).then((r) => r.data);
}

// POST /api/auth/login — Connexion et récupération du token JWT
export function login(payload) {
  return api.post("/auth/login", payload).then((r) => r.data);
}
