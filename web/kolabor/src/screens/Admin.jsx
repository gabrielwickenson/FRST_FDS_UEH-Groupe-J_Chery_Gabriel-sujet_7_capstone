import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { navigateTo } from "../router.jsx";
import kolaborLogoWhite from "../assets/kolabor-logo-white.svg";

function formatNombre(n) {
  return Number(n || 0).toLocaleString("fr-FR");
}

function Admin() {
  const [section, setSection] = React.useState("dashboard");
  const [litigesFilter, setLitigesFilter] = React.useState("ouverts");
  const { logout } = useAuth();
  const {
    me,
    adminKpis,
    adminKpisLoading,
    adminRevenusMensuels,
    adminTopCategories,
    adminUsers,
    adminUsersLoading,
    litigesOuverts,
    litigesOuvertsLoading,
    litigesOuvertsError,
    resoudreLitige,
    rejeterLitige,
    tousLitiges,
    tousLitigesLoading,
    tousLitigesError,
    validerPrestataire,
    suspendrePrestataire,
  } = useApp();

  const litigesAffiches = litigesFilter === "tous" ? tousLitiges : litigesOuverts;
  const litigesAffichesLoading = litigesFilter === "tous" ? tousLitigesLoading : litigesOuvertsLoading;
  const litigesAffichesError = litigesFilter === "tous" ? tousLitigesError : litigesOuvertsError;

  const litigeStatutStyle = {
    OUVERT: { background: "#FEF3C7", color: "#B45309" },
    RESOLU: { background: "#D8F3E4", color: "#0B5C36" },
    REJETE: { background: "#FEE2E2", color: "#B91C1C" },
  };
  const actionBtn = {
    padding: "6px 12px",
    borderRadius: 8,
    border: "1px solid #E5E7EB",
    background: "#fff",
    fontSize: 12.5,
    fontWeight: 700,
    cursor: "pointer",
    whiteSpace: "nowrap",
  };

  function handleLogout() {
    logout();
    navigateTo("login");
  }

  const maxRevenuMensuel = Math.max(1, ...adminRevenusMensuels.map((d) => d.montant));
  const deltaPct = adminKpis.deltaRevenusPct;
  const deltaLabel = deltaPct == null
    ? "Pas encore de comparaison"
    : `${deltaPct >= 0 ? "▲" : "▼"} ${Math.abs(deltaPct).toFixed(1)}% vs mois dernier`;

  const navItems = [
    { key: "dashboard", label: "Tableau de bord", icon: "fa-building" },
    { key: "users", label: "Utilisateurs", icon: "fa-user" },
    { key: "litiges", label: "Litiges", icon: "fa-triangle-exclamation" },
  ];

  return (
    <React.Fragment>
  <div className="k664">
    <aside className="k665">
      <div className="k666">
        <img src={kolaborLogoWhite} alt="Kolabor" style={{height: 28, width: "auto"}} />
        <div className="k669">
          ADMIN
        </div>
      </div>
      <div className="k429">
        {navItems.map((item) => (
<div
          key={item.key}
          className={section === item.key ? "k670" : "k671"}
          style={{cursor: "pointer"}}
          onClick={() => setSection(item.key)}
        >
          <i className={`icon fa-solid ${item.icon}`} style={{fontSize: "18px", color: "currentColor"}}></i>
          {item.label}
          {item.key === "litiges" && litigesOuverts.length > 0 ? (
<span style={{marginLeft: "auto", background: "#DC2626", color: "#fff", borderRadius: 999, fontSize: 11, fontWeight: 800, padding: "2px 7px"}}>
              {litigesOuverts.length}
            </span>
) : null}
        </div>
))}
        <div className="k671" style={{cursor: "pointer", marginTop: 16}} onClick={handleLogout}>
          <i className="icon fa-solid fa-right-from-bracket" style={{fontSize: "18px", color: "currentColor"}}></i>
          Se déconnecter
        </div>
      </div>
    </aside>
    <div>
      <div className="k190">
        <div>
          <h1 className="k493">
            {section === "dashboard" ? "Tableau de bord" : section === "users" ? "Utilisateurs" : "Litiges"}
          </h1>
          <p className="k494">
            Vue d'ensemble de la plateforme Kolabor.
          </p>
        </div>
        <div className="k672">
          <span className="k673">
            {(me.initials || "AD")}
          </span>
          <span className="k364">
            {me.nom || "Admin"}
          </span>
        </div>
      </div>

      {section === "dashboard" ? (
<React.Fragment>
      <div className="k433">
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Utilisateurs
            </span>
            <span className="k675">
              <i className="icon fa-solid fa-user" style={{fontSize: "17px", color: "#2563EB"}}></i>
            </span>
          </div>
          <div className="k676">
            {adminKpisLoading ? "…" : formatNombre(adminKpis.totalUtilisateurs)}
          </div>
          <div className="k498">
            {adminKpisLoading ? "" : `${formatNombre(adminKpis.totalClients)} clients · ${formatNombre(adminKpis.totalPrestataires)} pros`}
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Professionnels
            </span>
            <span className="k677">
              <i className="icon fa-solid fa-shield" style={{fontSize: "17px", color: "#139356"}}></i>
            </span>
          </div>
          <div className="k676">
            {adminKpisLoading ? "…" : formatNombre(adminKpis.totalPrestataires)}
          </div>
          <div className="k498">
            sur la plateforme
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Réservations
            </span>
            <span className="k678">
              <i className="icon fa-solid fa-calendar-days" style={{fontSize: "17px", color: "#F59E0B"}}></i>
            </span>
          </div>
          <div className="k676">
            {adminKpisLoading ? "…" : formatNombre(adminKpis.totalReservations)}
          </div>
          <div className="k498">
            {adminKpisLoading ? "" : `${formatNombre(adminKpis.litigesOuverts)} litige(s) ouvert(s)`}
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Revenus (mois)
            </span>
            <span className="k677">
              <i className="icon fa-solid fa-money-bill-wave" style={{fontSize: "17px", color: "#139356"}}></i>
            </span>
          </div>
          <div className="k679">
            {adminKpisLoading ? "…" : formatNombre(adminKpis.revenusMoisCourant)}
            <span className="k437">
              Gdes
            </span>
          </div>
          <div className="k498">
            {adminKpisLoading ? "" : deltaLabel}
          </div>
        </div>
      </div>
      <div className="k680">
        <div className="k681">
          <div className="k256">
            <h2 className="k505">
              Revenus (6 mois)
            </h2>
          </div>
          <div className="k683">
            {adminRevenusMensuels.length === 0 ? (
<p style={{color: "#6B7280"}}>Pas encore de données.</p>
) : adminRevenusMensuels.map((d, i) => (
<div key={i} className="k516">
              <div className="k684" style={{height: `${Math.max(4, (d.montant / maxRevenuMensuel) * 100)}px`}} title={`${d.montant} Gdes`}></div>
              <span className="k518">
                {d.mois}
              </span>
            </div>
))}
          </div>
        </div>
        <div className="k681">
          <h2 className="k514">
            Top catégories
          </h2>
          <div className="k406">
            {adminTopCategories.length === 0 ? (
<p style={{color: "#6B7280"}}>Pas encore de données.</p>
) : adminTopCategories.map((c, i) => (
<div key={i}>
              <div className="k690">
                <span className="k691">
                  {c.categorie}
                </span>
                <span className="k692">
                  {c.pourcentage.toFixed(0)}%
                </span>
              </div>
              <div className="k693">
                <span style={{display: "block", height: "100%", borderRadius: 999, width: `${Math.max(2, c.pourcentage)}%`, background: c.color}}></span>
              </div>
            </div>
))}
          </div>
        </div>
      </div>
      <div className="k699">
        <div className="k700">
          <h2 className="k505">
            Litiges ouverts
          </h2>
          <span className="k506" style={{cursor: "pointer"}} onClick={() => setSection("litiges")}>
            Tout voir
          </span>
        </div>
        <div className="k703">
          <span>
            Reservation
          </span>
          <span>
            Client
          </span>
          <span>
            Prestataire
          </span>
          <span>
            Motif
          </span>
          <span className="k28">
            Actions
          </span>
        </div>
        {litigesOuvertsLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement...</p>
) : litigesOuvertsError ? (
<p style={{color: "#B91C1C", padding: "16px 24px"}}>Impossible de charger les litiges depuis le serveur.</p>
) : litigesOuverts.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucun litige ouvert.</p>
) : litigesOuverts.slice(0, 5).map((l) => (
<div key={l.key} className="k704">
  <span className="k709">
    #{l.reservationId}
  </span>
  <span className="k709">
    {l.clientNom}
  </span>
  <span className="k709">
    {l.proNom}
  </span>
  <span className="k709">
    {l.motif}
  </span>
  <div className="k710">
    <button className="k711" onClick={() => resoudreLitige(l.litigeId)}>
      Resoudre
    </button>
  </div>
</div>
))}
      </div>
</React.Fragment>
) : null}

      {section === "users" ? (
<div className="k699">
        <div className="k703" style={{gridTemplateColumns: "1.8fr 1.6fr 0.9fr 0.9fr 1fr 150px"}}>
          <span>
            Nom
          </span>
          <span>
            Email
          </span>
          <span>
            Rôle
          </span>
          <span>
            Statut
          </span>
          <span>
            Inscrit le
          </span>
          <span className="k28">
            Actions
          </span>
        </div>
        {adminUsersLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement...</p>
) : adminUsers.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Aucun utilisateur pour le moment.</p>
) : adminUsers.map((u) => (
<div key={u.id} className="k704" style={{gridTemplateColumns: "1.8fr 1.6fr 0.9fr 0.9fr 1fr 150px"}}>
  <span className="k709" style={{display: "flex", alignItems: "center", gap: 8}}>
    <span style={{width: 28, height: 28, borderRadius: "50%", background: u.color, color: "#fff", fontSize: 11, fontWeight: 700, display: "inline-flex", alignItems: "center", justifyContent: "center", flexShrink: 0}}>
      {u.initials}
    </span>
    {u.name}
  </span>
  <span className="k709">
    {u.email}
  </span>
  <span className="k709" style={u.roleStyle}>
    {u.role}
  </span>
  <span>
    <span className={u.status === "Actif" ? "k756" : "k762"} style={u.suspendu ? {background: "#FEE2E2", color: "#B91C1C"} : undefined}>
      {u.status}
    </span>
  </span>
  <span className="k709">
    {u.dateInscription ? new Date(u.dateInscription).toLocaleDateString("fr-FR") : "—"}
  </span>
  <div className="k710">
    {u.isPrestataire ? (
      u.suspendu ? (
<button style={{...actionBtn, borderColor: "#139356", color: "#139356"}} onClick={() => validerPrestataire(u.id)}>
          Réactiver
        </button>
) : (
<button style={{...actionBtn, borderColor: "#FCA5A5", color: "#B91C1C"}} onClick={() => suspendrePrestataire(u.id, u.name)}>
          Suspendre
        </button>
)
    ) : (
<span style={{color: "#D1D5DB", fontSize: 12.5}}>—</span>
)}
  </div>
</div>
))}
      </div>
) : null}

      {section === "litiges" ? (
<React.Fragment>
      <div className="k757" style={{marginBottom: 16}}>
<button
          style={litigesFilter === "ouverts" ? {...actionBtn, background: "#0F274A", color: "#fff", borderColor: "#0F274A"} : actionBtn}
          onClick={() => setLitigesFilter("ouverts")}
        >
          Ouverts
        </button>
<button
          style={litigesFilter === "tous" ? {...actionBtn, background: "#0F274A", color: "#fff", borderColor: "#0F274A"} : actionBtn}
          onClick={() => setLitigesFilter("tous")}
        >
          Tous
        </button>
      </div>
<div className="k699">
        <div className="k703" style={{gridTemplateColumns: "1fr 1fr 1fr 1.4fr 120px 150px"}}>
          <span>
            Reservation
          </span>
          <span>
            Client
          </span>
          <span>
            Prestataire
          </span>
          <span>
            Motif
          </span>
          <span>
            Statut
          </span>
          <span className="k28">
            Actions
          </span>
        </div>
        {litigesAffichesLoading ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>Chargement...</p>
) : litigesAffichesError ? (
<p style={{color: "#B91C1C", padding: "16px 24px"}}>Impossible de charger les litiges depuis le serveur.</p>
) : litigesAffiches.length === 0 ? (
<p style={{color: "#6B7280", padding: "16px 24px"}}>{litigesFilter === "tous" ? "Aucun litige pour le moment." : "Aucun litige ouvert."}</p>
) : litigesAffiches.map((l) => (
<div key={l.key} className="k704" style={{gridTemplateColumns: "1fr 1fr 1fr 1.4fr 120px 150px"}}>
  <span className="k709">
    #{l.reservationId}
  </span>
  <span className="k709">
    {l.clientNom}
  </span>
  <span className="k709">
    {l.proNom}
  </span>
  <span className="k709">
    {l.motif}
    {l.statut !== "OUVERT" && l.resolution ? (
<span style={{display: "block", color: "#9CA3AF", fontSize: 12}}>→ {l.resolution}</span>
) : null}
  </span>
  <span>
    <span className="k756" style={litigeStatutStyle[l.statut] || litigeStatutStyle.OUVERT}>
      {l.statut === "RESOLU" ? "Résolu" : l.statut === "REJETE" ? "Rejeté" : "Ouvert"}
    </span>
  </span>
  <div className="k710">
    {l.statut === "OUVERT" ? (
<React.Fragment>
<button style={{...actionBtn, borderColor: "#139356", color: "#139356"}} onClick={() => resoudreLitige(l.litigeId)}>
        Résoudre
      </button>
<button style={{...actionBtn, borderColor: "#FCA5A5", color: "#B91C1C"}} onClick={() => rejeterLitige(l.litigeId)}>
        Rejeter
      </button>
</React.Fragment>
) : (
<span style={{color: "#D1D5DB", fontSize: 12.5}}>—</span>
)}
  </div>
</div>
))}
      </div>
</React.Fragment>
) : null}
    </div>
  </div>
    </React.Fragment>
  );
}

export default Admin;
