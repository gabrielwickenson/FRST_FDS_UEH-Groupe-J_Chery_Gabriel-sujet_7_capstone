import React from "react";
import { useApp } from "../AppContext.jsx";

function DashClient() {
  const [tab, setTab] = React.useState("upcoming");
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
    reservationsClient,
    reservationsClientLoading,
    reservationsClientError,
    clientStats,
    annulerReservation,
    laisserAvisSurReservation,
    me,
  } = useApp();
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
        <div className="k430">
          <i className="icon fa-solid fa-building" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Tableau de bord
        </div>
        <div className="k431">
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#fff"}}></i>
          Mes réservations
        </div>
        <div className="k430" onClick={nav.factures}>
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes paiements
        </div>
        <div className="k430" onClick={nav.params}>
          <i className="icon fa-solid fa-gear" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Paramètres
        </div>
      </div>
    </aside>
    <div>
      <h1 className="k229">
        Bonjour, {me.nom.split(" ")[0] || "vous"} 
      </h1>
      <p className="k432">
        Voici un aperçu de vos réservations.
      </p>
      <div className="k433">
        <div className="k434">
          <div className="k15">
            À venir
          </div>
          <div className="k435">
            {clientStats.upcoming}
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Terminées
          </div>
          <div className="k435">
            {clientStats.done}
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Réservations
          </div>
          <div className="k435">
            {reservationsClient.filter((r) => r.statut !== "ANNULEE").length}
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Total dépensé
          </div>
          <div className="k436">
            {clientStats.total}
            <span className="k437">
              Gdes
            </span>
          </div>
        </div>
      </div>
      <div className="k438">
        <button type="button" className={tab === "upcoming" ? "k439" : "k440"} onClick={() => setTab("upcoming")}>
          À venir
        </button>
        <button type="button" className={tab === "encours" ? "k439" : "k440"} onClick={() => setTab("encours")}>
          En cours
        </button>
        <button type="button" className={tab === "done" ? "k439" : "k441"} onClick={() => setTab("done")}>
          Terminées
        </button>
      </div>
      <div className="k442">
        {reservationsClientLoading ? (
<p style={{color: "#6B7280"}}>Chargement de vos réservations…</p>
) : reservationsClientError ? (
<p style={{color: "#B91C1C"}}>Impossible de charger vos réservations depuis le serveur.</p>
) : reservationsClient.length === 0 ? (
<p style={{color: "#6B7280"}}>Vous n'avez pas encore de réservation.</p>
) : reservationsClient.filter((r) => {
  if (tab === "encours") return r.statut === "EN_COURS";
  if (tab === "done") return r.statut === "TERMINEE" || r.statut === "TERMINE";
  return r.statut === "ACCEPTEE" || r.statut === "EN_ATTENTE" || r.statut === "PAYEE";
}).length === 0 ? (
<p style={{color: "#6B7280"}}>Aucune réservation dans cette catégorie.</p>
) : reservationsClient.filter((r) => {
  if (tab === "encours") return r.statut === "EN_COURS";
  if (tab === "done") return r.statut === "TERMINEE" || r.statut === "TERMINE";
  return r.statut === "ACCEPTEE" || r.statut === "EN_ATTENTE" || r.statut === "PAYEE";
}).map((r) => {
  const isDone = r.statut === "TERMINEE" || r.statut === "TERMINE";
  return (
<div key={r.key} className="k443">
          <div className="k444">
            <span className="k445"></span>
            <div className="k22">
              <div className="k190">
                <h3 className="k446">
                  {r.titre}
                </h3>
                <span className="k447">
                  {r.statutLabel}
                </span>
              </div>
              <div className="k448">
                avec {r.proNom}{r.proJob ? ` · ${r.proJob}` : ""}
              </div>
              <div className="k449">
                <span className="k450">
                  <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
                  {r.dateHeure}
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-location-dot" style={{fontSize: "16px", color: "#139356"}}></i>
                  {r.adresse}
                </span>
              </div>
            </div>
          </div>
          <div className="k451">
            {isDone ? (
<button className="k460" onClick={() => laisserAvisSurReservation(r.id)}>
              <i className="icon fa-solid fa-star" style={{fontSize: "15px", color: "#fff"}}></i>
              Laisser un avis
            </button>
) : (
<button className="k454" onClick={() => annulerReservation(r.id)}>
              Annuler
            </button>
)}
          </div>
        </div>
);
})}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default DashClient;
