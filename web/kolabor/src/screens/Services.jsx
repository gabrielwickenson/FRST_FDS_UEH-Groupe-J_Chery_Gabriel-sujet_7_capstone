import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Services() {
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
  <section className="k128">
    <div className="k129">
      <div className="k130">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          Services
        </span>
      </div>
      <h1 className="k133">
        Tous les services
      </h1>
      <p className="k134">
        Choisissez parmi des centaines de prestations réalisées par des professionnels vérifiés.
      </p>
      <div className="k135">
        <div className="k136">
          <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "20px", color: "#139356"}}></i>
          <input className="k137" placeholder="Rechercher un service\u2026" />
        </div>
        <button className="k138" onClick={nav.pros}>
          Rechercher
        </button>
      </div>
    </div>
  </section>
  <section className="k139">
    <div className="k140">
      <button className="k141">
        Tous
      </button>
      <button className="k142">
        Plomberie
      </button>
      <button className="k142">
        Électricité
      </button>
      <button className="k142">
        Peinture
      </button>
      <button className="k142">
        Jardinage
      </button>
      <button className="k142">
        Ménage
      </button>
      <button className="k142">
        Climatisation
      </button>
    </div>
    <div className="k143">
      {services.map((sv, __i) => (
<div key={sv.id ?? __i} className="k144">
  <div className="k145">
    <img className="k73" src={img(`svc-${sv.id}`, 800, 600)} alt={sv.cat} style={{objectFit: "cover"}} />
    <span className="k146" style={{color: sv.tag}}>
      {sv.cat}
    </span>
  </div>
  <div className="k147">
    <h3 className="k148">
      {sv.title}
    </h3>
    <div className="k79">
      <i className="icon fa-solid fa-star" style={{fontSize: "14px", color: "#F59E0B"}}></i>
      <span className="k149">
        {sv.rating}
      </span>
      · Intervention rapide
    </div>
    <div className="k150">
      <div>
        <span className="k27">
          À partir de
        </span>
        <div className="k151">
          {sv.price}
        </div>
      </div>
      <button className="k152" onClick={sv.open}>
        Voir
      </button>
    </div>
  </div>
</div>
))}
    </div>
  </section>
    </React.Fragment>
  );
}

export default Services;
