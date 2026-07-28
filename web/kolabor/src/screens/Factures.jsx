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
    me,
    reservationsClient,
    reservationsClientLoading,
  } = useApp();
  const factures = reservationsClient.filter((r) => r.statut !== "ANNULEE" && r.statut !== "ANNULE" && r.statut !== "EN_ATTENTE" && r.statut !== "ATTENTE");
  return (
    <React.Fragment>
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        {me.photoUrl ? (
<img src={me.photoUrl} alt="" className="k427" style={{objectFit: "cover"}} />
) : (
<span className="k427">
          {me.initials}
        </span>
)}
        <div>
          <div className="k23">
            {me.nom}
          </div>
          <div className="k428">
            {me.ville}
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k430" onClick={nav.dashclient}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes réservations
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
        {reservationsClientLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement…</p>
) : factures.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucune facture pour le moment.</p>
) : factures.map((f) => (
<div key={f.key} className="k730">
          <span className="k731">
            #{f.id}
          </span>
          <span className="k709">
            {f.titre} · {f.proNom}
          </span>
          <span className="k732">
            {f.dateHeure}
          </span>
          <span className="k364">
            {f.montant} Gdes
          </span>
          <div className="k733">
            <span style={{color: "#9CA3AF", fontSize: 13}}>{f.statutLabel}</span>
          </div>
        </div>
))}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Factures;
