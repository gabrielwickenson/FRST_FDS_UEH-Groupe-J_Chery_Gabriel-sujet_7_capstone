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
    reservationsProError,
    mesAvis,
    mesAvisLoading,
  } = useApp();
  // Historique : les clients qui ont RÉSERVÉ (ce sont eux qui vont donner
  // les revenus). Toutes les réservations actives apparaissent, avec leur
  // statut ; le montant passe en "+" une fois le paiement effectué.
  const estPaye = (r) => r.statut === "PAYEE" || r.statut === "EN_COURS" || r.statut === "TERMINEE";
  const transactions = reservationsPro
    .filter((r) => r.statut !== "ANNULEE" && r.statut !== "REFUSEE")
    .sort((a, b) => new Date(b.dateHeure) - new Date(a.dateHeure));
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
        <div className="k430" onClick={nav.params}>
          <i className="icon fa-solid fa-sliders" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Paramètres
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
        <div className="k775">
          <div className="k15">
            Nombre de prestations
          </div>
          <div className="k776">
            {proStats.nombrePrestations ?? reservationsPro.length}
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
) : reservationsProError ? (
<p style={{color: "#B91C1C", padding: "16px 24px"}}>Impossible de charger les réservations depuis le serveur (vérifiez que le backend est démarré et reconnectez-vous).</p>
) : transactions.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucune réservation en base pour ce compte prestataire.</p>
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
          <span className="k783" style={estPaye(t) ? undefined : {color: "#B45309"}}>
            {estPaye(t) ? `+${t.montant} Gdes` : `${t.montant} Gdes · ${t.statutLabel}`}
          </span>
        </div>
))}
      </div>
      <div className="k699" style={{marginTop: 24}}>
        <div className="k779">
          <h2 className="k505">
            Avis reçus ({mesAvis.length})
          </h2>
        </div>
        {mesAvisLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement…</p>
) : mesAvis.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucun avis pour le moment.</p>
) : mesAvis.map((a) => (
<div key={a.key} style={{padding: "14px 24px", borderTop: "1px solid #F3F4F6"}}>
          <div style={{display: "flex", alignItems: "center", gap: 10, marginBottom: 4}}>
            <span style={{fontWeight: 700, fontSize: 14, color: "#19355F"}}>
              {a.clientNom}
            </span>
            <span style={{color: "#F59E0B", fontSize: 14, letterSpacing: 1}}>
              {"★".repeat(Math.max(0, Math.min(5, Math.round(a.note))))}
              <span style={{color: "#E5E7EB"}}>{"★".repeat(Math.max(0, 5 - Math.round(a.note)))}</span>
            </span>
            <span style={{color: "#9CA3AF", fontSize: 12, marginLeft: "auto"}}>
              {a.date ? new Date(a.date).toLocaleDateString("fr-FR") : ""}
            </span>
          </div>
          {a.commentaire ? (
<p style={{fontSize: 13.5, color: "#4B5563"}}>{a.commentaire}</p>
) : null}
        </div>
))}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Revenus;
