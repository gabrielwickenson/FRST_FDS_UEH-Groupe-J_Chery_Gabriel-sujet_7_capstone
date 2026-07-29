import React from "react";
import { useApp } from "../AppContext.jsx";

function DashPro() {
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
    reservationsPro,
    reservationsProLoading,
    reservationsProError,
    reservationsProErrorDetail,
    demandesEnAttente,
    proStats,
    proRevenueWeek,
    mesAvis,
    accepterDemande,
    refuserDemande,
    terminerPrestation,
  } = useApp();
  // Tout est calculé automatiquement depuis les données réelles, avec repli :
  // si l'endpoint /statistiques ne répond pas (ou pas encore), chaque chiffre
  // est recalculé côté client à partir des réservations et avis chargés.
  const estPaye = (r) => r.statut === "PAYEE" || r.statut === "EN_COURS" || r.statut === "TERMINEE";
  // "Mes clients" = toutes les demandes de service en base (table
  // reservation) qui ne sont pas annulées/refusées — payées ou non. Seuls
  // les REVENUS restent calculés sur les réservations payées.
  const mesClients = reservationsPro.filter((r) => r.statut !== "ANNULEE" && r.statut !== "REFUSEE");
  const revenusCalcules = reservationsPro.filter(estPaye).reduce((s, r) => s + (Number(r.montant) || 0), 0);
  const revenusAffiches = proStats.totalRevenus ?? proStats.revenus ?? proStats.revenuTotal ?? proStats.revenu ?? revenusCalcules;
  const nbAvis = proStats.nombreAvis ?? mesAvis.length;
  const noteCalculee = mesAvis.length
    ? (mesAvis.reduce((s, a) => s + (Number(a.note) || 0), 0) / mesAvis.length).toFixed(1)
    : null;
  // Si le backend renvoie 0 (moyenne jamais recalculée) alors que des avis
  // existent, la moyenne est recalculée automatiquement depuis les avis.
  const noteBackend = proStats.noteMoyenne ?? proStats.moyenneNotes ?? proStats.note;
  const noteAffichee = (Number(noteBackend) > 0 ? noteBackend : null) ?? noteCalculee ?? "—";
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
        <div className="k431">
          <i className="icon fa-solid fa-building" style={{fontSize: "18px", color: "#fff"}}></i>
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
      <div className="k190">
        <div>
          <h1 className="k493">
            Bonjour, {me.nom.split(" ")[0] || "vous"} 
          </h1>
          <p className="k494">
            Voici votre activité cette semaine.
          </p>
        </div>
      </div>
      <div className="k433">
        <div className="k434">
          <div className="k15">
            Revenus
          </div>
          <div className="k496">
            {revenusAffiches}
            <span className="k497">
              Gdes
            </span>
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Note moyenne
          </div>
          <div className="k499">
            {noteAffichee}
            <span className="k501">
              ★
            </span>
          </div>
          <div className="k502">
            {nbAvis} avis
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Réservations
          </div>
          <div className="k499">
            {proStats.nombrePrestations ?? reservationsPro.length}
          </div>
          <div className="k502">
            au total
          </div>
        </div>
      </div>
      <div className="k503">
        <div className="k443">
          <div className="k504">
            <h2 className="k505">
              Mes clients
            </h2>
          </div>
          <div className="k248">
            {reservationsProLoading ? (
<p style={{color: "#6B7280"}}>Chargement…</p>
) : reservationsProError ? (
<p style={{color: "#B91C1C"}}>Erreur serveur : {reservationsProErrorDetail || "inconnue"}</p>
) : mesClients.length === 0 ? (
<p style={{color: "#6B7280"}}>Aucune réservation en base pour ce compte prestataire.</p>
) : mesClients.map((r) => (
<div key={r.key} className="k507">
              <span className="k508">
                {(r.clientNom || "?").split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase()}
              </span>
              <div className="k22">
                <div className="k471">
                  {r.clientNom}
                </div>
                <div className="k24">
                  {r.titre} · {r.adresse} · {r.dateHeure}
                </div>
              </div>
              {r.statut === "EN_ATTENTE" ? (
<div className="k509">
                <button className="k510" onClick={() => accepterDemande(r.id)}>
                  <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
                </button>
                <button className="k511" onClick={() => refuserDemande(r.id)}>
                  <i className="icon fa-solid fa-xmark" style={{fontSize: "15px", color: "currentColor"}}></i>
                </button>
              </div>
) : r.statut === "ACCEPTEE" || r.statut === "PAYEE" || r.statut === "EN_COURS" ? (
<div style={{display: "flex", alignItems: "center", gap: 8}}>
                <span className="k447">{r.statutLabel}</span>
                <button
                  style={{background: "#139356", color: "#fff", border: "none", borderRadius: 8, padding: "6px 12px", fontSize: 12, fontWeight: 700, cursor: "pointer"}}
                  onClick={() => terminerPrestation(r.id)}
                  title="Marquer la prestation comme terminée (comptée dans les revenus)"
                >
                  Terminer
                </button>
              </div>
) : (
<span className="k447">{r.statutLabel}</span>
)}
            </div>
))}
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default DashPro;
