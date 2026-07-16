import React from "react";
import { useApp } from "../AppContext.jsx";

function Header() {
  const { nav, navAccueil, navServices, navPros } = useApp();
  return (
    <header className="site-header">
  <div className="k785">
    <button className="k199" onClick={nav.accueil}>
      <span className="k786">
        <span className="k787"></span>
        <span className="k788">
          K
        </span>
      </span>
      <span className="k789">
        Kolabor
      </span>
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
      <button className="k794" onClick={nav.devenirPro}>
        Devenir pro
      </button>
      <button className="k795" onClick={nav.login}>
        Se connecter
      </button>
      <button className="k796" onClick={nav.signup}>
        S'inscrire
      </button>
    </div>
  </div>
    </header>
  );
}

export default Header;
