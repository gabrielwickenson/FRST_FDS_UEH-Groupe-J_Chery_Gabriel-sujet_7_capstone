import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { serviceImg } from "../images.js";

// Même normalisation que Reserver.jsx : compare la catégorie du service à
// la compétence du pro (accents/casse ignorés), pour rester cohérent avec
// le filtre "contains" appliqué côté backend.
function normalizeText(x) {
  return (x || "").toString().toLowerCase().normalize("NFD").replace(/[̀-ͯ]/g, "");
}

function Pros() {
  const { isAuthenticated } = useAuth();
  const [avisNote, setAvisNote] = React.useState(0);
  const [avisHover, setAvisHover] = React.useState(0);
  const [avisCommentaire, setAvisCommentaire] = React.useState("");
  const [avisSent, setAvisSent] = React.useState(false);
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
    prosLoading,
    prosError,
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
    selectedProId,
    setSelectedProId,
    selectedPro,
    selectedProAvis,
    selectedProAvisQuery,
    canReviewSelectedPro,
    laisserAvisSurProfil,
    laisserAvisProfilMutation,
    authUserId,
  } = useApp();

  // La page dédiée /#/profil a été retirée : voir le détail d'un pro (bio +
  // services + avis) et lui laisser un avis se fait maintenant dans une
  // boîte de dialogue (popup), directement sur /pros, sans changer d'écran.
  function openProDialog(id) {
    setSelectedProId(id);
    setAvisNote(0);
    setAvisHover(0);
    setAvisCommentaire("");
    setAvisSent(false);
  }

  function closeProDialog() {
    setSelectedProId(null);
  }

  const dialogPro = selectedProId != null
    ? (filteredPros.find((p) => String(p.id) === String(selectedProId))
        || allPros.find((p) => String(p.id) === String(selectedProId)))
    : null;
  const dialogProCat = dialogPro ? normalizeText(dialogPro.cat) : "";
  const dialogProServices = dialogProCat
    ? services.filter((s) => {
        const cat = normalizeText(s.cat);
        return cat.includes(dialogProCat) || dialogProCat.includes(cat);
      })
    : [];
  return (
    <React.Fragment>
  <section className="k128">
    <div className="k168">
      <div className="k169">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          Professionnels
        </span>
      </div>
      <h1 className="k170">
        {prosHeading}
      </h1>
      <div className="k171">
        <div className="k172">
          <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "18px", color: "#139356"}}></i>
          <input className="k173" value={prosSearch} onChange={onProsSearch} onKeyDown={onProsKey} placeholder="M\u00e9tier ou service" />
        </div>
        <div className="k174"></div>
        <div className="k175">
          <i className="icon fa-solid fa-location-dot" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          <input className="k173" value={prosCity} onChange={onProsCity} onKeyDown={onProsKey} placeholder="Ville" />
        </div>
        <button className="k176" onClick={applyProsSearch}>
          Rechercher
        </button>
      </div>
    </div>
  </section>
  <section className="k177">
    <aside className="k178">
      <div className="k179">
        <span className="k26">
          Filtres
        </span>
        <button className="k180" onClick={resetFilters}>
          Réinitialiser
        </button>
      </div>
      <div className="k181">
        <div className="k182">
          Catégorie
        </div>
        {filterCatUI.map((fc, __i) => (
<label key={fc.id ?? __i} className="k183" onClick={fc.toggle}>
  <span style={fc.boxStyle}>
    {fc.checked ? (
<React.Fragment>
      <i className="icon fa-solid fa-check" style={{fontSize: "13px", color: "#fff"}}></i>
</React.Fragment>
) : null}
  </span>
  {fc.name}
</label>
))}
      </div>
      <div className="k184">
        <div className="k182">
          Note minimale
        </div>
        <div className="k185">
          <label className="k186" onClick={setRating4}>
            <span style={radio4Style}></span>
            <span className="k187">
              ★★★★
            </span>
            & plus
          </label>
          <label className="k186" onClick={setRating3}>
            <span style={radio3Style}></span>
            <span className="k187">
              ★★★
            </span>
            & plus
          </label>
        </div>
      </div>
      <div className="k184">
        <div className="k190">
          <span className="k189">
            Vérifié uniquement
          </span>
          <span onClick={toggleVerified} style={verifTrackStyle}>
            <span style={verifKnobStyle}></span>
          </span>
        </div>
      </div>
    </aside>
    <div>
      <div className="k197">
        <span className="k198">
          <strong className="k132">
            {prosCount} professionnels
          </strong>
          trouvés
        </span>
        <div className="k199">
          <span className="k200">
            Trier :
          </span>
          <div className="k201">
            <select className="k202">
              <option>
                Mieux notés
              </option>
              <option>
                Prix croissant
              </option>
              <option>
                Prix décroissant
              </option>
              <option>
                Plus proche
              </option>
              <option>
                Plus d'avis
              </option>
            </select>
            <i className="icon fa-solid fa-chevron-down k203" style={{fontSize: "14px", color: "#6B7280"}}></i>
          </div>
        </div>
      </div>
      <div className="k204">
        {prosLoading ? (
<p style={{gridColumn: "1 / -1", color: "#6B7280"}}>Chargement des professionnels…</p>
) : prosError ? (
<p style={{gridColumn: "1 / -1", color: "#B91C1C"}}>Impossible de charger les professionnels depuis le serveur.</p>
) : filteredPros.map((p, __i) => (
<div key={p.id ?? __i} className="k144">
  <div className="k205">
    <img className="k73" src={p.photoUrl || serviceImg(p.job, p.cat, p.id, 800, 600)} alt={p.name} style={{objectFit: "cover"}} />
    {p.available ? (
<React.Fragment>
      <span className="k206">
        <span className="k207"></span>
        Disponible
      </span>
</React.Fragment>
) : null}
  </div>
  <div className="k208">
    {p.photoUrl ? (
<img src={p.photoUrl} alt="" className="k209" style={{objectFit: "cover"}} />
) : (
<span className="k209" style={{background: p.avatar}}>
      {p.initials}
    </span>
)}
    <div className="k77">
      <span className="k46">
        {p.name}
      </span>
      <i className="icon fa-solid fa-shield-halved" style={{fontSize: "15px", color: "#22C55E"}}></i>
    </div>
    <div className="k78">
      {p.job} · {p.city}
    </div>
    <div className="k210">
      <i className="icon fa-solid fa-star" style={{fontSize: "15px", color: "#F59E0B"}}></i>
      <span className="k149">
        {p.rating}
      </span>
      <span className="k211">
        ({p.reviews} avis)
      </span>
    </div>
    <div className="k25">
      <div className="k212">
        {p.price}
      </div>
      <button className="k213" onClick={() => openProDialog(p.id)}>
        Ajouter un avis
      </button>
    </div>
  </div>
</div>
))}
      </div>
      {noPros ? (
<React.Fragment>
        <div className="k214">
          <span className="k215">
            <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "30px", color: "#9CA3AF"}}></i>
          </span>
          <h3 className="k66">
            Aucun professionnel trouvé
          </h3>
          <p className="k216">
            Essayez d'élargir votre recherche ou de réinitialiser les filtres.
          </p>
          <button className="k217" onClick={resetFilters}>
            Réinitialiser les filtres
          </button>
        </div>
</React.Fragment>
) : null}
      <div className="k218">
        <button className="k219">
          ‹
        </button>
        <button className="k220">
          1
        </button>
        <button className="k221">
          2
        </button>
        <button className="k221">
          3
        </button>
        <button className="k221">
          ›
        </button>
      </div>
    </div>
  </section>
  {dialogPro ? (
<div
  style={{position: "fixed", inset: 0, background: "rgba(15,23,42,0.55)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000, padding: 16}}
  onClick={closeProDialog}
>
  <div
    style={{background: "#fff", borderRadius: 16, maxWidth: 520, width: "100%", maxHeight: "85vh", overflowY: "auto", padding: 24, position: "relative"}}
    onClick={(e) => e.stopPropagation()}
  >
    <button
      type="button"
      onClick={closeProDialog}
      aria-label="Fermer"
      style={{position: "absolute", top: 14, right: 14, background: "none", border: "none", fontSize: 20, color: "#6B7280", cursor: "pointer"}}
    >
      ×
    </button>
    <div style={{display: "flex", alignItems: "center", gap: 12, marginBottom: 16}}>
      {dialogPro.photoUrl ? (
<img src={dialogPro.photoUrl} alt="" className="k209" style={{objectFit: "cover"}} />
) : (
<span className="k209" style={{background: dialogPro.avatar}}>{dialogPro.initials}</span>
)}
      <div>
        <div className="k46">{dialogPro.name}</div>
        <div className="k78">{dialogPro.job} · {dialogPro.city}</div>
      </div>
    </div>
    <h3 style={{fontSize: 14, fontWeight: 700, color: "#19355F", marginBottom: 6}}>À propos</h3>
    {selectedPro?.bio ? (
<p style={{fontSize: 13.5, color: "#4B5563", marginBottom: 16}}>{selectedPro.bio}</p>
) : (
<p style={{fontSize: 13.5, color: "#9CA3AF", marginBottom: 16}}>Ce professionnel n'a pas encore renseigné de description.</p>
)}
    <h3 style={{fontSize: 14, fontWeight: 700, color: "#19355F", marginBottom: 6}}>Services proposés</h3>
    {dialogProServices.length === 0 ? (
<p style={{fontSize: 13.5, color: "#9CA3AF", marginBottom: 16}}>Aucun service correspondant pour le moment.</p>
) : (
<div style={{display: "flex", flexWrap: "wrap", gap: 8, marginBottom: 16}}>
        {dialogProServices.map((s) => (
<span key={s.id} className="k246">{s.title}</span>
))}
      </div>
)}
    <h3 style={{fontSize: 14, fontWeight: 700, color: "#19355F", marginBottom: 6}}>Avis clients ({dialogPro.reviews})</h3>
    <div style={{marginBottom: 16}}>
      {selectedProAvisQuery.isLoading ? (
<p style={{color: "#6B7280", fontSize: 13.5}}>Chargement des avis…</p>
) : (selectedProAvis || []).length === 0 ? (
<p style={{color: "#9CA3AF", fontSize: 13.5}}>Aucun avis pour le moment.</p>
) : selectedProAvis.slice(0, 5).map((a, __j) => (
<div key={a.identifiant ?? __j} style={{marginBottom: 10}}>
              <div className="k20">
                <span className="k262">
                  {"★".repeat(Math.round(a.note || 0))}
                </span>
                <span className="k95">
                  {a.date || ""}
                </span>
              </div>
              <p style={{fontSize: 13.5, color: "#4B5563"}}>{a.commentaire}</p>
            </div>
))}
    </div>
    {avisSent ? (
<p style={{color: "#139356", fontSize: 14, fontWeight: 600}}>Merci, votre avis a été publié.</p>
) : canReviewSelectedPro ? (
<React.Fragment>
      <div style={{fontSize: 14, fontWeight: 700, color: "#19355F", marginBottom: 8}}>
        Laisser un avis sur ce professionnel
      </div>
      <div style={{display: "flex", gap: 6, marginBottom: 10}}>
        {[1, 2, 3, 4, 5].map((n) => (
<i
            key={n}
            className="icon fa-solid fa-star"
            onMouseEnter={() => setAvisHover(n)}
            onMouseLeave={() => setAvisHover(0)}
            onClick={() => setAvisNote(n)}
            style={{fontSize: "22px", cursor: "pointer", color: (avisHover || avisNote) >= n ? "#F59E0B" : "#D1D5DB"}}
          ></i>
))}
      </div>
      <textarea
        className="k386"
        placeholder="Votre commentaire (optionnel)"
        value={avisCommentaire}
        onChange={(e) => setAvisCommentaire(e.target.value)}
        style={{width: "100%", marginBottom: 10}}
      ></textarea>
      {laisserAvisProfilMutation.isError ? (
<p style={{color: "#B91C1C", fontSize: 13, marginBottom: 10}}>
          {laisserAvisProfilMutation.error?.response?.data?.error || "Impossible d'envoyer l'avis. Réessayez."}
        </p>
) : null}
      <button
        type="button"
        className="k253"
        disabled={!avisNote || laisserAvisProfilMutation.isPending}
        onClick={() => {
          if (!avisNote) return;
          laisserAvisSurProfil(avisNote, avisCommentaire.trim(), {
            onSuccess: () => {
              setAvisSent(true);
              setAvisNote(0);
              setAvisCommentaire("");
            },
          });
        }}
      >
        {laisserAvisProfilMutation.isPending ? "Envoi..." : "Publier l'avis"}
      </button>
</React.Fragment>
) : isAuthenticated && selectedProId && authUserId && String(selectedProId) === String(authUserId) ? (
<p style={{color: "#9CA3AF", fontSize: 13}}>Vous ne pouvez pas laisser un avis sur votre propre profil.</p>
) : (
<p style={{color: "#9CA3AF", fontSize: 13}}>Connectez-vous pour laisser un avis sur ce professionnel.</p>
)}
  </div>
</div>
) : null}
    </React.Fragment>
  );
}

export default Pros;
