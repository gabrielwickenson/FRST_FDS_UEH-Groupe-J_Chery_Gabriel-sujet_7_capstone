import React from "react";
import { useApp } from "../AppContext.jsx";

function Revenus() {
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
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        <span className="k427">
          MF
        </span>
        <div>
          <div className="k23">
            Marc Fontaine
          </div>
          <div className="k489">
            Pro · Vérifié
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k430" onClick={nav.dashpro}>
          <i className="icon fa-solid fa-building" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Tableau de bord
        </div>
        <div className="k430" onClick={nav.messervices}>
          <i className="icon fa-solid fa-table-columns" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes services
        </div>
        <div className="k430" onClick={nav.dispos}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Disponibilités
        </div>
        <div className="k431">
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "#fff"}}></i>
          Revenus
        </div>
        <div className="k430" onClick={nav.messages}>
          <i className="icon fa-solid fa-message" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Messagerie
        </div>
      </div>
    </aside>
    <div>
      <h1 className="k493">
        Revenus
      </h1>
      <p className="k494">
        Suivez vos gains et vos versements.
      </p>
      <div className="k769">
        <div className="k770">
          <div className="k771">
            Solde disponible
          </div>
          <div className="k772">
            12 800
            <span className="k773">
              Gdes
            </span>
          </div>
          <button className="k774">
            Retirer
          </button>
        </div>
        <div className="k775">
          <div className="k15">
            Revenus du mois
          </div>
          <div className="k776">
            18 400
            <span className="k437">
              Gdes
            </span>
          </div>
          <div className="k777">
            ▲ 12% vs mois dernier
          </div>
        </div>
        <div className="k775">
          <div className="k15">
            Total encaissé
          </div>
          <div className="k776">
            142 600
            <span className="k437">
              Gdes
            </span>
          </div>
          <div className="k778">
            Depuis janvier 2025
          </div>
        </div>
      </div>
      <div className="k699">
        <div className="k779">
          <h2 className="k505">
            Historique des transactions
          </h2>
        </div>
        <div className="k780">
          <span>
            Service
          </span>
          <span>
            Client
          </span>
          <span>
            Date
          </span>
          <span className="k28">
            Montant
          </span>
        </div>
        <div className="k781">
          <span className="k782">
            Réparation de fuite
          </span>
          <span className="k732">
            Sophie Martin
          </span>
          <span className="k732">
            15 Jan
          </span>
          <span className="k783">
            +250 Gdes
          </span>
        </div>
        <div className="k781">
          <span className="k782">
            Débouchage canalisation
          </span>
          <span className="k732">
            Ricardo Joseph
          </span>
          <span className="k732">
            13 Jan
          </span>
          <span className="k783">
            +200 Gdes
          </span>
        </div>
        <div className="k781">
          <span className="k782">
            Retrait vers MonCash
          </span>
          <span className="k732">
            —
          </span>
          <span className="k732">
            10 Jan
          </span>
          <span className="k784">
            −5 000 Gdes
          </span>
        </div>
        <div className="k781">
          <span className="k782">
            Installation sanitaire
          </span>
          <span className="k732">
            Gladys Charles
          </span>
          <span className="k732">
            8 Jan
          </span>
          <span className="k783">
            +850 Gdes
          </span>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Revenus;
