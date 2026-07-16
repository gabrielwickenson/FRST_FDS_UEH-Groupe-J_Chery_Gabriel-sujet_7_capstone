import React from "react";
import { useApp } from "../AppContext.jsx";

function Factures() {
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
          PJ
        </span>
        <div>
          <div className="k23">
            Peter Joseph
          </div>
          <div className="k428">
            Pétion-Ville
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k430" onClick={nav.dashclient}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes réservations
        </div>
        <div className="k430" onClick={nav.favoris}>
          <i className="icon fa-solid fa-heart" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes favoris
        </div>
        <div className="k430" onClick={nav.messages}>
          <i className="icon fa-solid fa-message" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Messagerie
        </div>
        <div className="k431">
          <i className="icon fa-solid fa-file-lines" style={{fontSize: "18px", color: "#fff"}}></i>
          Factures
        </div>
        <div className="k430" onClick={nav.params}>
          <i className="icon fa-solid fa-sliders" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Paramètres
        </div>
      </div>
    </aside>
    <div>
      <h1 className="k493">
        Factures
      </h1>
      <p className="k494">
        Retrouvez et téléchargez toutes vos factures.
      </p>
      <div className="k699">
        <div className="k729">
          <span>
            N° facture
          </span>
          <span>
            Service
          </span>
          <span>
            Date
          </span>
          <span>
            Montant
          </span>
          <span className="k28">
            Facture
          </span>
        </div>
        <div className="k730">
          <span className="k731">
            #KLB-0147
          </span>
          <span className="k709">
            Réparation fuite · Marc Fontaine
          </span>
          <span className="k732">
            15 Jan 2026
          </span>
          <span className="k364">
            400 Gdes
          </span>
          <div className="k733">
            <button className="k734">
              PDF
            </button>
          </div>
        </div>
        <div className="k730">
          <span className="k731">
            #KLB-0132
          </span>
          <span className="k709">
            Nettoyage maison · Roselène Pierre
          </span>
          <span className="k732">
            9 Jan 2026
          </span>
          <span className="k364">
            150 Gdes
          </span>
          <div className="k733">
            <button className="k734">
              PDF
            </button>
          </div>
        </div>
        <div className="k730">
          <span className="k731">
            #KLB-0098
          </span>
          <span className="k709">
            Entretien climatiseur · Wesley Dorvil
          </span>
          <span className="k732">
            28 Déc 2025
          </span>
          <span className="k364">
            400 Gdes
          </span>
          <div className="k733">
            <button className="k734">
              PDF
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Factures;
