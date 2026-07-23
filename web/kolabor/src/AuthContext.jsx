import React from "react";
import * as authApi from "./api/auth.js";
import { getUserId, getUserRole } from "./utils/field.js";

const AuthContext = React.createContext(null);

function useAuth() {
  const ctx = React.useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé sous <AuthProvider>");
  return ctx;
}

function readStoredUser() {
  try {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

// Extrait le token JWT et l'utilisateur depuis une réponse de login/register,
// quelle que soit la forme exacte renvoyée par le backend.
function extractAuthPayload(data) {
  if (!data) return { token: null, user: null };
  const token = data.token || data.accessToken || data.jwt || null;
  const user = data.utilisateur || data.user || data.prestataire || data.client || data;
  return { token, user };
}

function AuthProvider({ children }) {
  const [token, setToken] = React.useState(() => localStorage.getItem("token"));
  const [user, setUser] = React.useState(readStoredUser);
  const [authLoading, setAuthLoading] = React.useState(false);
  const [authError, setAuthError] = React.useState("");

  const persist = React.useCallback((nextToken, nextUser) => {
    if (nextToken) localStorage.setItem("token", nextToken);
    else localStorage.removeItem("token");
    if (nextUser) localStorage.setItem("user", JSON.stringify(nextUser));
    else localStorage.removeItem("user");
    setToken(nextToken || null);
    setUser(nextUser || null);
  }, []);

  const login = React.useCallback(async ({ email, motDePasse }) => {
    setAuthLoading(true);
    setAuthError("");
    try {
      const data = await authApi.login({ "e-mail": email, motDePasse });
      const { token: t, user: u } = extractAuthPayload(data);
      persist(t, u);
      return u;
    } catch (err) {
      const msg = err?.response?.data?.message || err?.response?.data?.erreur || "Identifiants invalides. Vérifiez votre e-mail et votre mot de passe.";
      setAuthError(msg);
      throw err;
    } finally {
      setAuthLoading(false);
    }
  }, [persist]);

  const register = React.useCallback(async (payload) => {
    setAuthLoading(true);
    setAuthError("");
    try {
      const data = await authApi.register(payload);
      const { token: t, user: u } = extractAuthPayload(data);
      // Certaines API ne renvoient pas de token à l'inscription : dans ce
      // cas on ne connecte pas automatiquement, l'appelant peut rediriger
      // vers /login.
      if (t) persist(t, u);
      return u;
    } catch (err) {
      const msg = err?.response?.data?.message || err?.response?.data?.erreur || "Impossible de créer le compte. Vérifiez les informations saisies.";
      setAuthError(msg);
      throw err;
    } finally {
      setAuthLoading(false);
    }
  }, [persist]);

  const logout = React.useCallback(() => {
    persist(null, null);
  }, [persist]);

  const value = React.useMemo(() => ({
    token,
    user,
    userId: getUserId(user),
    role: getUserRole(user),
    isAuthenticated: !!token,
    isClient: getUserRole(user) === "CLIENT" || getUserRole(user) === "client",
    isPro: getUserRole(user) === "PRESTATAIRE" || getUserRole(user) === "prestataire" || getUserRole(user) === "PRO",
    isAdmin: getUserRole(user) === "ADMIN" || getUserRole(user) === "admin",
    authLoading,
    authError,
    setAuthError,
    login,
    register,
    logout,
  }), [token, user, authLoading, authError, login, register, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export { AuthProvider, useAuth };
