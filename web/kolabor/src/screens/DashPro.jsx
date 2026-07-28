import React from "react";
import { useApp } from "../AppContext.jsx";

function DashPro() {
  // Bascule entre "Demandes reçues" (uniquement EN_ATTENTE, avec boutons
  // accepter/refuser) et la liste complète de tous les clients qui ont
  // réservé avec ce pro (tous statuts), sans jamais quitter /dashpro — le
  // lien "Mes réservations (client)" ci-dessous sert à un usage différent
  // (le pro réserve LUI-MÊME un service ailleurs en tant que client) et ne
  // doit pas être confondu avec "voir qui a réservé avec moi".
  const [showAllClients, setShowAllClients] = React.useState(false);

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
    demandesEnAttente,
    proStats,
    proRevenueWeek,
    accepterDemande,
    refuserDemande,
  } = useApp();
  const maxRevenue = Math.max(1, ...proRevenueWeek.map((d) => d.montant));
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
        <div className="k490" style={{cursor: "pointer"}} onClick={() => setShowAllClients(false)}>
          <span className="k491">
            <i className="icon fa-solid fa-heart-pulse" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
            Demandes reçues
          </span>
          <span className="k492">
            {demandesEnAttente.length}
          </span>
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
            Bonjour, {me.nom.split(" ")[0] || "vous"} 👋
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
            {proStats.revenus ?? proStats.revenuTotal ?? proStats.revenu ?? 0}
            <span className="k497">
              Gdes
            </span>
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Demandes en attente
          </div>
          <div className="k499">
            {demandesEnAttente.length}
          </div>
          <div className="k500">
            À traiter
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Note moyenne
          </div>
          <div className="k499">
            {proStats.moyenneNotes ?? proStats.note ?? "—"}
            <span className="k501">
              ★
            </span>
          </div>
          <div className="k502">
            {proStats.nombreAvis ?? 0} avis
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
              {showAllClients ? "Tous mes clients" : "Demandes reçues"}
            </h2>
            <span className="k506" style={{cursor: "pointer"}} onClick={() => setShowAllClients((v) => !v)}>
              {showAllClients ? "Voir les demandes" : "Tout voir"}
            </span>
          </div>
          <div className="k248">
            {reservationsProLoading ? (
<p style={{color: "#6B7280"}}>Chargement…</p>
) : (showAllClients ? reservationsPro : demandesEnAttente).length === 0 ? (
<p style={{color: "#6B7280"}}>{showAllClients ? "Aucun client pour le moment." : "Aucune demande en attente."}</p>
) : (showAllClients ? reservationsPro : demandesEnAttente).map((r) => (
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
) : (
<span className="k447">{r.statutLabel}</span>
)}
            </div>
))}
          </div>
        </div>
        <div className="k443">
          <h2 className="k514">
            Revenus (7 jours)
          </h2>
          <div className="k515">
            {proRevenueWeek.length === 0 ? (
<p style={{color: "#6B7280"}}>Pas encore de données.</p>
) : proRevenueWeek.map((d, __i) => (
<div key={__i} className="k516">
              <div className="k517" style={{height: `${Math.max(4, (d.montant / maxRevenue) * 100)}px`}} title={`${d.montant} Gdes`}></div>
              <span className="k518">
                {d.jour}
              </span>
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
