import React from "react";
import { useApp } from "../AppContext.jsx";

function Dispos() {
  const [jourAdd, setJourAdd] = React.useState("Lundi");
  const [debutAdd, setDebutAdd] = React.useState("08:00");
  const [finAdd, setFinAdd] = React.useState("18:00");

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
    me,
    disposList,
    disposLoading,
    ajouterDisponibilite,
    supprimerDisponibilite,
  } = useApp();
  return (
    <React.Fragment>
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        <span className="k427">
          {me.initials}
        </span>
        <div>
          <div className="k23">
            {me.nom}
          </div>
          <div className="k489">
            Pro
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
        <div className="k430" onClick={nav.params}>
          <i className="icon fa-solid fa-sliders" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Paramètres
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
        {disposLoading ? (
<p style={{color: "#6B7280"}}>Chargement…</p>
) : disposList.length === 0 ? (
<p style={{color: "#6B7280"}}>Aucune disponibilité enregistrée pour le moment.</p>
) : disposList.map((d) => (
<div key={d.key} className="k764">
  <div className="k765">
    <span className="k374">
      {d.jour}
    </span>
  </div>
  <div className="k199">
      <div className="k766">
        {d.heureDebut}
      </div>
      <span className="k211">
        —
      </span>
      <div className="k766">
        {d.heureFin}
      </div>
    </div>
  <button className="k767" onClick={() => supprimerDisponibilite(d.id)}>
    Supprimer
  </button>
</div>
))}
        <div className="k764">
          <div className="k765">
            <select className="k339" value={jourAdd} onChange={(e) => setJourAdd(e.target.value)}>
              {["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"].map((j) => (
<option key={j} value={j}>{j}</option>
))}
            </select>
          </div>
          <div className="k199">
            <input type="time" className="k766" value={debutAdd} onChange={(e) => setDebutAdd(e.target.value)} />
            <span className="k211">—</span>
            <input type="time" className="k766" value={finAdd} onChange={(e) => setFinAdd(e.target.value)} />
          </div>
        </div>
        <button className="k768" onClick={() => ajouterDisponibilite({ jour: jourAdd, heureDebut: debutAdd, heureFin: finAdd })}>
          Ajouter cette disponibilité
        </button>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Dispos;
