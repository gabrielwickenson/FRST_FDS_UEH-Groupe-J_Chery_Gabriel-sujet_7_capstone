import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Favoris() {
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
        <div className="k431">
          <i className="icon fa-solid fa-heart" style={{fontSize: "18px", color: "#fff"}}></i>
          Mes favoris
        </div>
        <div className="k430" onClick={nav.messages}>
          <i className="icon fa-solid fa-message" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Messagerie
        </div>
        <div className="k430" onClick={nav.factures}>
          <i className="icon fa-solid fa-file-lines" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
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
        Mes favoris
      </h1>
      <p className="k494">
        Les professionnels que vous avez enregistrés.
      </p>
      <div className="k720">
        {pros.map((p, __i) => (
<div key={p.id ?? __i} className="k721">
  <div className="k722">
    <img className="k73" src={img(`pcover-${p.id}`, 800, 600)} alt={p.name} style={{objectFit: "cover"}} />
    <button className="k723">
      <i className="icon fa-solid fa-heart" style={{fontSize: "17px", color: "#DC2626"}}></i>
    </button>
  </div>
  <div className="k724">
    <span className="k725" style={{background: p.avatar}}>
      {p.initials}
    </span>
    <div className="k726">
      {p.name}
    </div>
    <div className="k24">
      {p.job} · {p.city}
    </div>
    <div className="k727">
      <i className="icon fa-solid fa-star" style={{fontSize: "14px", color: "#F59E0B"}}></i>
      <span className="k149">
        {p.rating}
      </span>
      <span className="k211">
        ({p.reviews})
      </span>
    </div>
    <button className="k728" onClick={p.open}>
      Voir le profil
    </button>
  </div>
</div>
))}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Favoris;
