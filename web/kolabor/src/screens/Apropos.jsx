import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Apropos() {
  const {
    calDays,
    isRoleClient,
    isRolePro,
    setRoleClient,
    setRolePro,
    roleClientStyle,
    roleProStyle,
    services,
    allPros,
    screen,
    isAccueil,
    isServices,
    isPros,
    isProfil,
    isService,
    isComment,
    isLogin,
    isSignup,
    isReserver,
    isPaiement,
    isConfirm,
    isDashClient,
    isMessages,
    isDashPro,
    isAdmin,
    isCatalogue,
    isProLanding,
    isApropos,
    isTarifs,
    isBlog,
    isAide,
    isContact,
    isFaq,
    isLegal,
    isNotFound,
    isFavoris,
    isFactures,
    isParams,
    isMesServices,
    isDispos,
    isRevenus,
    catFilter,
    prosSearch,
    prosCity,
    onProsSearch,
    onProsCity,
    heroSearchVal,
    heroCityVal,
    onHeroSearch,
    onHeroCity,
    onHeroKey,
    applyProsSearch,
    onProsKey,
    resetFilters,
    filteredPros,
    prosCount,
    noPros,
    prosHeading,
    filterCatUI,
    radio4Style,
    radio3Style,
    setRating4,
    setRating3,
    availTrackStyle,
    availKnobStyle,
    verifTrackStyle,
    verifKnobStyle,
    toggleAvail,
    toggleVerified,
    jours,
    adminUsers,
    metiers,
    zonesHaiti,
    faqList,
    catFilters,
    catFilterList,
    filteredCatalogue,
    catalogue,
    convList,
    activeConv,
    draft,
    onDraft,
    onKey,
    sendMsg,
    nav,
    navAccueil,
    navServices,
    navPros,
    pros,
    featured,
  } = useApp();
  return (
    <React.Fragment>
  <section className="k153">
    <div className="k571">
      <div className="k526">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          À propos
        </span>
      </div>
      <span className="k61">
        Notre histoire
      </span>
      <h1 className="k572">
        Rapprocher les Haïtiens des meilleurs professionnels
      </h1>
      <p className="k573">
        Kolabor est né d'un constat simple : trouver un professionnel fiable pour ses travaux à domicile en Haïti est souvent long et incertain. Nous avons créé une plateforme de confiance pour changer cela.
      </p>
    </div>
  </section>
  <section className="k574">
    <img className="k575" src={img("about-cover", 800, 600)} alt="Photo \u2014 \u00e9quipe / professionnels Kolabor" style={{objectFit: "cover", borderRadius: "24px"}} />
  </section>
  <section className="k576">
    <div className="k35">
      <div className="k577">
        2024
      </div>
      <div className="k37">
        Année de création
      </div>
    </div>
    <div className="k35">
      <div className="k577">
        12 000+
      </div>
      <div className="k37">
        Professionnels
      </div>
    </div>
    <div className="k35">
      <div className="k577">
        32
      </div>
      <div className="k37">
        Villes couvertes
      </div>
    </div>
    <div className="k35">
      <div className="k578">
        85 000+
      </div>
      <div className="k37">
        Services réalisés
      </div>
    </div>
  </section>
  <section className="k579">
    <h2 className="k580">
      Nos valeurs
    </h2>
    <div className="k82">
      <div className="k552">
        <span className="k84">
          <i className="icon fa-solid fa-shield-halved" style={{fontSize: "26px", color: "#139356"}}></i>
        </span>
        <h3 className="k66">
          Confiance
        </h3>
        <p className="k67">
          Chaque professionnel est vérifié. La confiance est au cœur de tout ce que nous faisons.
        </p>
      </div>
      <div className="k552">
        <span className="k85">
          <i className="icon fa-solid fa-clock" style={{fontSize: "26px", color: "#2563EB"}}></i>
        </span>
        <h3 className="k66">
          Proximité
        </h3>
        <p className="k67">
          Des professionnels locaux, proches de chez vous, qui connaissent votre région.
        </p>
      </div>
      <div className="k552">
        <span className="k86">
          <i className="icon fa-solid fa-star" style={{fontSize: "26px", color: "#F59E0B"}}></i>
        </span>
        <h3 className="k66">
          Excellence
        </h3>
        <p className="k67">
          Nous valorisons le travail bien fait et récompensons les meilleurs professionnels.
        </p>
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Apropos;
