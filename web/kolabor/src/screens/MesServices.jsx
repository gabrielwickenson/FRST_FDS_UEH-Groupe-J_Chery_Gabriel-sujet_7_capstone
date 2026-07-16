import React from "react";
import { useApp } from "../AppContext.jsx";

function MesServices() {
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
        <div className="k431">
          <i className="icon fa-solid fa-table-columns" style={{fontSize: "18px", color: "#fff"}}></i>
          Mes services
        </div>
        <div className="k430" onClick={nav.dispos}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Disponibilités
        </div>
        <div className="k430" onClick={nav.revenus}>
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Revenus
        </div>
        <div className="k430" onClick={nav.messages}>
          <i className="icon fa-solid fa-message" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Messagerie
        </div>
      </div>
    </aside>
    <div>
      <div className="k190">
        <div>
          <h1 className="k493">
            Mes services
          </h1>
          <p className="k494">
            Gérez les prestations que vous proposez.
          </p>
        </div>
        <button className="k495">
          <i className="icon fa-solid fa-plus" style={{fontSize: "17px", color: "#fff"}}></i>
          Ajouter un service
        </button>
      </div>
      <div className="k752">
        <div className="k753">
          <span className="k754"></span>
          <div className="k22">
            <div className="k46">
              Réparation de fuite d'eau
            </div>
            <div className="k78">
              Intervention rapide · 1-2h
            </div>
          </div>
          <div className="k755">
            250 Gdes
          </div>
          <span className="k756">
            Actif
          </span>
          <div className="k757">
            <button className="k758">
              <i className="icon fa-solid fa-pen" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
            <button className="k759">
              <i className="icon fa-solid fa-trash" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
          </div>
        </div>
        <div className="k753">
          <span className="k760"></span>
          <div className="k22">
            <div className="k46">
              Débouchage de canalisation
            </div>
            <div className="k78">
              Intervention rapide · 1h
            </div>
          </div>
          <div className="k755">
            200 Gdes
          </div>
          <span className="k756">
            Actif
          </span>
          <div className="k757">
            <button className="k758">
              <i className="icon fa-solid fa-pen" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
            <button className="k759">
              <i className="icon fa-solid fa-trash" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
          </div>
        </div>
        <div className="k753">
          <span className="k761"></span>
          <div className="k22">
            <div className="k46">
              Installation sanitaire complète
            </div>
            <div className="k78">
              Sur devis · demi-journée
            </div>
          </div>
          <div className="k755">
            Sur devis
          </div>
          <span className="k762">
            En pause
          </span>
          <div className="k757">
            <button className="k758">
              <i className="icon fa-solid fa-pen" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
            <button className="k759">
              <i className="icon fa-solid fa-trash" style={{fontSize: "15px", color: "currentColor"}}></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default MesServices;
