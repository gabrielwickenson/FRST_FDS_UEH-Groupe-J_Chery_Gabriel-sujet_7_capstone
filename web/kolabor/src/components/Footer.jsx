import React from "react";
import { useApp } from "../AppContext.jsx";

function Footer() {
  const { nav } = useApp();
  return (
    <footer className="site-footer">
  <div className="k797">
    <div className="k798">
      <div>
        <div className="k799">
          <span className="k800">
            K
          </span>
          <span className="k801">
            Kolabor
          </span>
        </div>
        <p className="k802">
          La marketplace de services à domicile qui connecte les Haïtiens aux meilleurs professionnels.
        </p>
      </div>
      <div>
        <div className="k803">
          Services
        </div>
        <div className="k804">
          <span className="k805" onClick={nav.filtrer.Plomberie}>
            Plomberie
          </span>
          <span className="k805" onClick={nav.filtrer.Electricite}>
            Électricité
          </span>
          <span className="k805" onClick={nav.filtrer.Menage}>
            Entretien de la maison
          </span>
          <span className="k805" onClick={nav.filtrer.Jardinage}>
            Jardinage
          </span>
          <span className="k805" onClick={nav.tousServices}>
            Tous les services
          </span>
        </div>
      </div>
      <div>
        <div className="k803">
          Entreprise
        </div>
        <div className="k804">
          <span className="k805" onClick={nav.apropos}>
            À propos
          </span>
          <span className="k805" onClick={nav.comment}>
            Comment ça marche
          </span>
          <span className="k805" onClick={nav.tarifs}>
            Tarifs
          </span>
          <span className="k805" onClick={nav.blog}>
            Blog
          </span>
        </div>
      </div>
      <div>
        <div className="k803">
          Support
        </div>
        <div className="k804">
          <span className="k805" onClick={nav.aide}>
            Centre d'aide
          </span>
          <span className="k805" onClick={nav.contact}>
            Contact
          </span>
          <span className="k805" onClick={nav.faq}>
            FAQ
          </span>
          <span className="k805" onClick={nav.dashpro}>
            Espace pro
          </span>
          <span className="k805" onClick={nav.legal}>
            Conditions générales
          </span>
        </div>
      </div>
    </div>
    <div className="k806">
      <span>
        © 2026 Kolabor. Tous droits réservés.
      </span>
      <div className="k807">
        <span className="k805" onClick={nav.legal}>
          Confidentialité
        </span>
        <span className="k805" onClick={nav.legal}>
          Mentions légales
        </span>
        <span className="k805" onClick={nav.legal}>
          Cookies
        </span>
        <span className="k805" onClick={nav.admin}>
          Espace admin
        </span>
      </div>
    </div>
  </div>
    </footer>
  );
}

export default Footer;
