import React from "react";
import { useApp } from "../AppContext.jsx";

function Tarifs() {
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
    <div className="k581">
      <div className="k526">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          Tarifs
        </span>
      </div>
      <h1 className="k582">
        Des tarifs clairs et transparents
      </h1>
      <p className="k156">
        Réserver un service est gratuit. Vous ne payez que la prestation, sans frais cachés.
      </p>
    </div>
  </section>
  <section className="k583">
    <div className="k584">
      <h3 className="k257">
        Particulier
      </h3>
      <p className="k585">
        Pour réserver des services
      </p>
      <div className="k586">
        <span className="k587">
          Gratuit
        </span>
      </div>
      <div className="k588">
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Réservation illimitée
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Messagerie avec les pros
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Paiement sécurisé
        </div>
      </div>
      <button className="k589" onClick={nav.signup}>
        Créer un compte
      </button>
    </div>
    <div className="k590">
      <span className="k591">
        POPULAIRE
      </span>
      <h3 className="k257">
        Pro
      </h3>
      <p className="k585">
        Pour les professionnels
      </p>
      <div className="k586">
        <span className="k587">
          10%
        </span>
        <span className="k592">
          / commission
        </span>
      </div>
      <div className="k588">
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Profil professionnel
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Demandes illimitées
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Statistiques & revenus
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Paiement sous 48h
        </div>
      </div>
      <button className="k593" onClick={nav.devenirPro}>
        Devenir pro
      </button>
    </div>
    <div className="k584">
      <h3 className="k257">
        Entreprise
      </h3>
      <p className="k585">
        Pour les organisations
      </p>
      <div className="k586">
        <span className="k587">
          Sur devis
        </span>
      </div>
      <div className="k588">
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Compte multi-utilisateurs
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Facturation centralisée
        </div>
        <div className="k290">
          <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
          Support dédié
        </div>
      </div>
      <button className="k589" onClick={nav.contact}>
        Nous contacter
      </button>
    </div>
  </section>
  <section className="k594">
    <p className="k198">
      Des questions sur nos tarifs ?
      <span className="k318" onClick={nav.faq}>
        Consultez la FAQ
      </span>
    </p>
  </section>
    </React.Fragment>
  );
}

export default Tarifs;
