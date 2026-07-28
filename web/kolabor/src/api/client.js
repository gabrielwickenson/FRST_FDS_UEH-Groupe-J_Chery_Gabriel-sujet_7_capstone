import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // URL de votre backend (adaptez si besoin)
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT à chaque requête
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercepteur de réponse : si le token n'est plus valide (401), on nettoie la session
// pour éviter de rester bloqué avec un token expiré.
//
// IMPORTANT : ce module n'est pas un composant React et ne peut donc pas
// appeler useAuth()/logout() directement. Sans notifier l'app, l'état
// React `isAuthenticated`/`userId` restait "connecté" en mémoire alors que
// le token venait d'être supprimé du localStorage — l'utilisateur voyait
// une interface toujours connectée mais dont CHAQUE appel suivant échouait
// silencieusement en 401 (ex. impossible de finaliser un panier), sans
// aucune indication qu'il fallait se reconnecter. On émet donc un évènement
// que AuthContext écoute pour aligner son état et rediriger vers /login.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      const hadToken = !!localStorage.getItem('token');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (hadToken) {
        window.dispatchEvent(new Event('kolabor:unauthorized'));
      }
    }
    return Promise.reject(error);
  }
);

export default api;