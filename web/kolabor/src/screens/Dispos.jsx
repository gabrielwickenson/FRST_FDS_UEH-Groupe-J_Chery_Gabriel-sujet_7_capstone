import React from "react";
import { useApp } from "../AppContext.jsx";

function Dispos() {
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
        <div className="k431">
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#fff"}}></i>
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
      <h1 className="k493">
        Mes disponibilités
      </h1>
      <p className="k494">
        Définissez vos horaires de travail pour chaque jour.
      </p>
      <div className="k763">
        {jours.map((j, __i) => (
<div key={j.id ?? __i} className="k764">
  <div className="k765">
    <span style={j.toggleStyle}>
      <span style={j.knobStyle}></span>
    </span>
    <span className="k374">
      {j.name}
    </span>
  </div>
  {j.open ? (
<React.Fragment>
    <div className="k199">
      <div className="k766">
        08h00
      </div>
      <span className="k211">
        —
      </span>
      <div className="k766">
        18h00
      </div>
    </div>
</React.Fragment>
) : null}
  {j.closed ? (
<React.Fragment>
    <span className="k767">
      Indisponible
    </span>
</React.Fragment>
) : null}
</div>
))}
        <button className="k768">
          Enregistrer les disponibilités
        </button>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Dispos;
