import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Catalogue() {
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
    <div className="k525">
      <div className="k526">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          Toutes les catégories
        </span>
      </div>
      <h1 className="k133">
        Tous les services Kolabor
      </h1>
      <p className="k527">
        Plus de 24 catégories et des centaines de prestations à domicile, réalisées par des professionnels vérifiés partout en Haïti.
      </p>
      <div className="k528">
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
  <section className="k529">
    <div className="k530">
      {catFilterList.map((f, __i) => (
<button key={f.id ?? __i} onClick={f.pick} style={f.chipStyle}>
  {f.label}
</button>
))}
    </div>
    <div className="k531">
      {filteredCatalogue.map((cat, __i) => (
<div key={cat.id ?? __i} className="k144">
  <div className="k532">
    <img className="k73" src={img(`img-${cat.slug}`, 800, 600)} alt={cat.name} style={{objectFit: "cover"}} />
    <span className="k146" style={{color: cat.color}}>
      {cat.name}
    </span>
  </div>
  <div className="k147">
    <h3 className="k148">
      {cat.name}
    </h3>
    <div className="k79">
      <i className="icon fa-solid fa-star" style={{fontSize: "14px", color: "#F59E0B"}}></i>
      <span className="k149">
        {cat.rating}
      </span>
      · {cat.count} pros
    </div>
    <div className="k150">
      <div>
        <span className="k27">
          À partir de
        </span>
        <div className="k151">
          {cat.price}
        </div>
      </div>
      <button className="k152" onClick={cat.open}>
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

export default Catalogue;
