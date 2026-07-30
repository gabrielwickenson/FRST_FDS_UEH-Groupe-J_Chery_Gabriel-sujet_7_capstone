import React from "react";
import { useApp } from "../AppContext.jsx";
import { serviceImg } from "../images.js";

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
    servicesLoading,
    servicesError,
    noServices,
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
    filteredServices,
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
    setSelectedServiceId,
  } = useApp();

  function ajouterAuPanierDepuisCatalogue(sv) {
    setSelectedServiceId(sv.id);
    nav.reserver();
  }

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
    <div>
      {servicesLoading ? (
        <p style={{color: "#6B7280"}}>Chargement des services…</p>
      ) : servicesError ? (
        <p style={{color: "#B91C1C"}}>Impossible de charger le catalogue depuis le serveur.</p>
      ) : noServices ? (
        <p style={{color: "#6B7280"}}>Aucun service disponible pour le moment.</p>
      ) : filteredCatalogue.length === 0 ? (
        <p style={{color: "#6B7280"}}>Aucun service disponible dans cette catégorie pour le moment.</p>
      ) : filteredCatalogue.map((cat, __ci) => {
        const catServices = filteredServices.filter((sv) => sv.cat === cat.name);
        if (catServices.length === 0) return null;
        return (
<div key={cat.slug ?? __ci} style={{marginBottom: 40}}>
  <div style={{display: "flex", alignItems: "baseline", gap: 10, marginBottom: 16}}>
    <h2 style={{fontSize: 22, fontWeight: 800, color: "#19355F", margin: 0}}>
      {cat.name}
    </h2>
    <span style={{fontSize: 13.5, color: "#9CA3AF", fontWeight: 600}}>
      {catServices.length} service{catServices.length > 1 ? "s" : ""}
    </span>
  </div>
  <div className="k531">
    {catServices.map((sv, __i) => (
<div key={sv.id ?? __i} className="k144">
  <div className="k145">
    <img className="k73" src={serviceImg(sv.title, sv.cat, sv.id, 800, 600)} alt={sv.cat} style={{objectFit: "cover"}} />
    <span className="k146" style={{color: sv.tag}}>
      {sv.cat}
    </span>
  </div>
  <div className="k147">
    <h3 className="k148">
      {sv.title}
    </h3>
    <div className="k79">
      {sv.description}
    </div>
    <div className="k150" style={{gap: 8}}>
      <button className="k266" onClick={sv.open} style={{width: "auto", margin: 0, flex: 1}}>
        Voir le détail
      </button>
      <button className="k152" onClick={() => ajouterAuPanierDepuisCatalogue(sv)} style={{flex: 1}}>
        Ajouter au panier
      </button>
    </div>
  </div>
</div>
))}
  </div>
</div>
        );
      })}
    </div>
  </section>
    </React.Fragment>
  );
}

export default Catalogue;
