import api from "./client.js";

// GET /api/users/{id} — Obtenir les informations d'un utilisateur (profil)
export function getUser(id) {
  return api.get(`/users/${id}`).then((r) => r.data);
}

// GET /api/users/{id}/photo — Récupérer la photo de profil d'un utilisateur
export function getUserPhotoUrl(id) {
  // Route brute utilisable directement comme src d'une <img>, avec le token déjà géré
  // côté serveur d'assets si besoin (endpoint public le plus souvent).
  return `${api.defaults.baseURL}/users/${id}/photo`;
}

// POST /api/users/{id}/photo — Uploader une photo de profil
export function uploadUserPhoto(id, file) {
  const form = new FormData();
  form.append("photo", file);
  return api
    .post(`/users/${id}/photo`, form, { headers: { "Content-Type": "multipart/form-data" } })
    .then((r) => r.data);
}

// PUT /api/users/fcm-token — Enregistrer le token FCM pour les notifications push
export function updateFcmToken(fcmToken) {
  return api.put("/users/fcm-token", { fcmToken }).then((r) => r.data);
}
