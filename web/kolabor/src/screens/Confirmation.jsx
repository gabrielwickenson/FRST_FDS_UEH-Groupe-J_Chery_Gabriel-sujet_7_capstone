import React from "react";
import { useApp } from "../AppContext.jsx";

function Confirmation() {
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
  <div className="k411">
    <span className="k412">
      <span className="k413">
        <i className="icon fa-solid fa-check" style={{fontSize: "32px", color: "#fff"}}></i>
      </span>
    </span>
    <h1 className="k229">
      Réservation confirmée !
    </h1>
    <p className="k414">
      Votre réservation avec
      <strong className="k132">
        Marc Fontaine
      </strong>
      est confirmée. Un e-mail de confirmation vous a été envoyé.
    </p>
    <div className="k415">
      <div className="k416">
        <span className="k15">
          N° de réservation
        </span>
        <span className="k417">
          #KLB-2026-0147
        </span>
      </div>
      <div className="k418">
        <div className="k20">
          <span className="k96">
            MF
          </span>
          <div>
            <div className="k23">
              Marc Fontaine
            </div>
            <div className="k95">
              Réparation de fuite d'eau
            </div>
          </div>
        </div>
        <div className="k419">
          <div className="k420">
            <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
            15 Jan 2026
          </div>
          <div className="k420">
            <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#139356"}}></i>
            14h – 16h
          </div>
        </div>
      </div>
    </div>
    <div className="k421">
      <button className="k422" onClick={nav.dashclient}>
        Voir mes réservations
      </button>
      <button className="k423" onClick={nav.accueil}>
        Retour à l'accueil
      </button>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Confirmation;
