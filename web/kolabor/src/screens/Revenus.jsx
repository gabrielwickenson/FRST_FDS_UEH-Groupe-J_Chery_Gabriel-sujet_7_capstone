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
    me,
    proStats,
    proRevenueWeek,
    reservationsPro,
    reservationsProLoading,
  } = useApp();
  const revenusSemaine = proRevenueWeek.reduce((sum, d) => sum + (d.montant || 0), 0);
  const transactions = reservationsPro.filter((r) => r.statut === "TERMINEE" || r.statut === "TERMINE" || r.statut === "CONFIRMEE" || r.statut === "CONFIRME");
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
            Revenus (7 derniers jours)
          </div>
          <div className="k772">
            {revenusSemaine}
            <span className="k773">
              Gdes
            </span>
          </div>
        </div>
        <div className="k775">
          <div className="k15">
            Nombre de prestations
          </div>
          <div className="k776">
            {proStats.nombrePrestations ?? reservationsPro.length}
          </div>
        </div>
        <div className="k775">
          <div className="k15">
            Total encaissé
          </div>
          <div className="k776">
            {proStats.revenus ?? proStats.revenuTotal ?? proStats.revenu ?? 0}
            <span className="k437">
              Gdes
            </span>
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
        {reservationsProLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement…</p>
) : transactions.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucune transaction pour le moment.</p>
) : transactions.map((t) => (
<div key={t.key} className="k781">
          <span className="k782">
            {t.titre}
          </span>
          <span className="k732">
            {t.clientNom}
          </span>
          <span className="k732">
            {t.dateHeure}
          </span>
          <span className="k783">
            +{t.montant} Gdes
          </span>
        </div>
))}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Revenus;
