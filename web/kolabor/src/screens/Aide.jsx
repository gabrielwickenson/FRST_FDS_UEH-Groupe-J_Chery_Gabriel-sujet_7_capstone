import React from "react";
import { useApp } from "../AppContext.jsx";

function Aide() {
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
  <section className="k533">
    <div className="k622"></div>
    <div className="k623">
      <h1 className="k624">
        Comment pouvons-nous vous aider ?
      </h1>
      <div className="k625">
        <div className="k12">
          <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "20px", color: "#139356"}}></i>
          <input className="k626" placeholder="Rechercher dans l'aide\u2026" />
        </div>
        <button className="k138">
          Rechercher
        </button>
      </div>
    </div>
  </section>
  <section className="k627">
    <div className="k531">
      <div className="k628">
        <span className="k629">
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "24px", color: "#139356"}}></i>
        </span>
        <h3 className="k556">
          Réservations
        </h3>
        <p className="k630">
          Réserver, modifier ou annuler un service.
        </p>
      </div>
      <div className="k628">
        <span className="k631">
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "24px", color: "#2563EB"}}></i>
        </span>
        <h3 className="k556">
          Paiements
        </h3>
        <p className="k630">
          Moyens de paiement, factures et remboursements.
        </p>
      </div>
      <div className="k628">
        <span className="k632">
          <i className="icon fa-solid fa-user" style={{fontSize: "24px", color: "#F59E0B"}}></i>
        </span>
        <h3 className="k556">
          Mon compte
        </h3>
        <p className="k630">
          Profil, sécurité et paramètres du compte.
        </p>
      </div>
      <div className="k628">
        <span className="k633">
          <i className="icon fa-solid fa-user" style={{fontSize: "24px", color: "#7C3AED"}}></i>
        </span>
        <h3 className="k556">
          Devenir professionnel
        </h3>
        <p className="k630">
          Inscription, profil pro et vérification.
        </p>
      </div>
      <div className="k628">
        <span className="k634">
          <i className="icon fa-solid fa-shield-halved" style={{fontSize: "24px", color: "#DC2626"}}></i>
        </span>
        <h3 className="k556">
          Sécurité & litiges
        </h3>
        <p className="k630">
          Signaler un problème et résoudre un litige.
        </p>
      </div>
      <div className="k628">
        <span className="k629">
          <i className="icon fa-solid fa-message" style={{fontSize: "24px", color: "#139356"}}></i>
        </span>
        <h3 className="k556">
          Nous contacter
        </h3>
        <p className="k630">
          Besoin d'aide ? Notre équipe vous répond.
        </p>
      </div>
    </div>
    <div className="k635">
      <div>
        <h3 className="k257">
          Vous ne trouvez pas votre réponse ?
        </h3>
        <p className="k636">
          Notre équipe support est disponible 7j/7.
        </p>
      </div>
      <button className="k637" onClick={nav.contact}>
        Contacter le support
      </button>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Aide;
