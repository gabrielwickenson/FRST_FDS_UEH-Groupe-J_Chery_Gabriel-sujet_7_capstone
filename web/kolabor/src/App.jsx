import React from "react";
import { AppProvider, useApp } from "./AppContext.jsx";
import { AuthProvider, useAuth } from "./AuthContext.jsx";
import { navigateTo } from "./router.jsx";
import Header from "./components/Header.jsx";
import Footer from "./components/Footer.jsx";
import Accueil from "./screens/Accueil.jsx";
import Services from "./screens/Services.jsx";
import Comment from "./screens/Comment.jsx";
import Pros from "./screens/Pros.jsx";
import Profil from "./screens/Profil.jsx";
import ServiceDetail from "./screens/ServiceDetail.jsx";
import Login from "./screens/Login.jsx";
import Signup from "./screens/Signup.jsx";
import Reserver from "./screens/Reserver.jsx";
import Paiement from "./screens/Paiement.jsx";
import Confirmation from "./screens/Confirmation.jsx";
import DashClient from "./screens/DashClient.jsx";
import DashPro from "./screens/DashPro.jsx";
import Catalogue from "./screens/Catalogue.jsx";
import ProLanding from "./screens/ProLanding.jsx";
import Apropos from "./screens/Apropos.jsx";
import Tarifs from "./screens/Tarifs.jsx";
import Blog from "./screens/Blog.jsx";
import Aide from "./screens/Aide.jsx";
import Contact from "./screens/Contact.jsx";
import Faq from "./screens/Faq.jsx";
import Legal from "./screens/Legal.jsx";
import Admin from "./screens/Admin.jsx";
import NotFound from "./screens/NotFound.jsx";
import Factures from "./screens/Factures.jsx";
import Parametres from "./screens/Parametres.jsx";
import MesServices from "./screens/MesServices.jsx";
import Dispos from "./screens/Dispos.jsx";
import Revenus from "./screens/Revenus.jsx";

const SCREEN_COMPONENTS = {
  accueil: Accueil,
  services: Services,
  comment: Comment,
  pros: Pros,
  profil: Profil,
  service: ServiceDetail,
  login: Login,
  signup: Signup,
  reserver: Reserver,
  paiement: Paiement,
  confirm: Confirmation,
  dashclient: DashClient,
  dashpro: DashPro,
  catalogue: Catalogue,
  prolanding: ProLanding,
  apropos: Apropos,
  tarifs: Tarifs,
  blog: Blog,
  aide: Aide,
  contact: Contact,
  faq: Faq,
  legal: Legal,
  admin: Admin,
  notfound: NotFound,
  factures: Factures,
  params: Parametres,
  messervices: MesServices,
  dispos: Dispos,
  revenus: Revenus,
};

// Écrans qui nécessitent d'être connecté. Un utilisateur non authentifié qui
// arrive sur l'une de ces pages (URL tapée directement, lien partagé, etc.)
// est renvoyé vers la connexion.
const PROTECTED_SCREENS = new Set([
  "dashclient", "dashpro", "admin", "params",
  "messervices", "dispos", "revenus", "factures",
]);

// Écrans réservés à un rôle précis. Un utilisateur connecté mais avec le
// mauvais rôle est renvoyé vers son propre tableau de bord plutôt que
// bloqué complètement.
const ROLE_SCREENS = {
  dashclient: "CLIENT",
  factures: "CLIENT",
  dashpro: "PRESTATAIRE",
  messervices: "PRESTATAIRE",
  dispos: "PRESTATAIRE",
  revenus: "PRESTATAIRE",
  admin: "ADMIN",
};

function defaultScreenForRole(role) {
  const r = (role || "").toString().toUpperCase();
  if (r === "PRESTATAIRE") return "dashpro";
  if (r === "ADMIN") return "admin";
  return "dashclient";
}

function AppShell() {
  const { screen } = useApp();
  const { isAuthenticated, role } = useAuth();

  React.useEffect(() => {
    if (PROTECTED_SCREENS.has(screen) && !isAuthenticated) {
      navigateTo("login");
      return;
    }
    const requiredRole = ROLE_SCREENS[screen];
    if (requiredRole && isAuthenticated && (role || "").toString().toUpperCase() !== requiredRole) {
      navigateTo(defaultScreenForRole(role));
    }
  }, [screen, isAuthenticated, role]);

  const isAllowed = !PROTECTED_SCREENS.has(screen) || (
    isAuthenticated && (!ROLE_SCREENS[screen] || (role || "").toString().toUpperCase() === ROLE_SCREENS[screen])
  );
  const Screen = isAllowed ? (SCREEN_COMPONENTS[screen] || NotFound) : (() => null);
  return (
    <div className="app-shell">
      <Header />
      <main>
        <Screen />
      </main>
      <Footer />
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppProvider>
        <AppShell />
      </AppProvider>
    </AuthProvider>
  );
}

export default App;
