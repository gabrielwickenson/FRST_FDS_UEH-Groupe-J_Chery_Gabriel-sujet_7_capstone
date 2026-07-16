import React from "react";
import { AppProvider, useApp } from "./AppContext.jsx";
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
import Messagerie from "./screens/Messagerie.jsx";
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
import Favoris from "./screens/Favoris.jsx";
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
  messages: Messagerie,
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
  favoris: Favoris,
  factures: Factures,
  params: Parametres,
  messervices: MesServices,
  dispos: Dispos,
  revenus: Revenus,
};

function AppShell() {
  const { screen } = useApp();
  const Screen = SCREEN_COMPONENTS[screen] || NotFound;
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
    <AppProvider>
      <AppShell />
    </AppProvider>
  );
}

export default App;
