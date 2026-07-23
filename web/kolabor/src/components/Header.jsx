import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { navigateTo } from "../router.jsx";
import kolaborLogo from "../assets/kolabor-logo.svg";

function Header() {
  const { nav, navAccueil, navServices, navPros, me, screen } = useApp();
  const { isAuthenticated, isPro, isAdmin, logout } = useAuth();
  const [menuOpen, setMenuOpen] = React.useState(false);

  React.useEffect(() => {
    setMenuOpen(false);
  }, [screen]);

  function handleLogout() {
    logout();
    navigateTo("accueil");
  }

  return (
    <header className="site-header">
  <div className={`k785${menuOpen ? " nav-open" : ""}`}>
    <button className="k199" onClick={nav.accueil}>
      <img src={kolaborLogo} alt="Kolabor" className="site-logo" />
    </button>
    <button
      type="button"
      className="burger-btn"
      aria-label={menuOpen ? "Fermer le menu" : "Ouvrir le menu"}
      onClick={() => setMenuOpen((v) => !v)}
    >
      <i className={`icon fa-solid ${menuOpen ? "fa-xmark" : "fa-bars"}`} style={{fontSize: "22px"}}></i>
    </button>
    <nav className="k790">
      <button className="k791" onClick={nav.accueil} style={{color: navAccueil}}>
        Accueil
      </button>
      <button className="k791" onClick={nav.tousServices} style={{color: navServices}}>
        Services
      </button>
      <button className="k791" onClick={nav.pros} style={{color: navPros}}>
        Professionnels
      </button>
      <button className="k792" onClick={nav.comment}>
        Comment ça marche
      </button>
    </nav>
    <div className="k793">
      {isAuthenticated ? (
<React.Fragment>
      <button className="k795" onClick={() => navigateTo(isAdmin ? "admin" : isPro ? "dashpro" : "dashclient")}>
        {me.nom || "Mon compte"}
      </button>
      <button className="k796" onClick={handleLogout}>
        Se déconnecter
      </button>
</React.Fragment>
) : (
<React.Fragment>
      <button className="k794" onClick={nav.devenirPro}>
        Devenir pro
      </button>
      <button className="k795" onClick={nav.login}>
        Se connecter
      </button>
      <button className="k796" onClick={nav.signup}>
        S'inscrire
      </button>
</React.Fragment>
)}
    </div>
  </div>
    </header>
  );
}

export default Header;
