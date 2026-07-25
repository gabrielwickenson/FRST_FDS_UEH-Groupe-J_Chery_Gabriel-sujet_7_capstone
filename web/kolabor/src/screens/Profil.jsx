import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Profil() {
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
    selectedPro,
    selectedProStats,
    selectedProAvis,
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
  } = useApp();
  const pro = selectedPro || {};
  const proName = pro.name || "Prestataire";
  const proReviews = selectedProStats?.nombrePrestations ?? pro.reviews ?? 0;
  const proRating = selectedProStats?.moyenneNotes ?? pro.rating ?? "—";
  return (
    <React.Fragment>
  <div className="k222">
    <img className="k73" src={img("profil-cover", 800, 600)} alt="Photo de couverture du professionnel" style={{objectFit: "cover"}} />
    <div className="k223">
      <div className="k224">
        <span className="k131" onClick={nav.pros}>
          Professionnels
        </span>
        /  {proName}
      </div>
    </div>
  </div>
  <section className="k225">
    <div className="k226">
      <span className="k227">
        {pro.initials || "PR"}
      </span>
      <div className="k228">
        <div className="k199">
          <h1 className="k229">
            {proName}
          </h1>
          {pro.available ? (
<span className="k230">
            <i className="icon fa-solid fa-shield-halved" style={{fontSize: "14px", color: "#22C55E"}}></i>
            Disponible
          </span>
) : null}
        </div>
        <div className="k232">
          {pro.job || "Prestataire"}
        </div>
        <div className="k233">
          <div className="k234">
            <i className="icon fa-solid fa-star" style={{fontSize: "16px", color: "#F59E0B"}}></i>
            <strong className="k235">
              {proRating}
            </strong>
            <span className="k211">
              ({proReviews} avis)
            </span>
          </div>
          <div className="k236">
            <i className="icon fa-solid fa-location-dot" style={{fontSize: "16px", color: "#9CA3AF"}}></i>
            {pro.city || "Haïti"}
          </div>
          {pro.available ? (
<div className="k237">
            <span className="k4"></span>
            Disponible aujourd'hui
          </div>
) : null}
        </div>
      </div>
      <div className="k238">
        <button className="k239" onClick={nav.reserver}>
          Réserver
        </button>
      </div>
    </div>
    <div className="k241">
      <div className="k242">
        <div className="k88">
          <h2 className="k243">
            À propos
          </h2>
          <p className="k244">
            Plombier certifié depuis 8 ans, j'interviens rapidement pour tous vos travaux de plomberie : fuites, débouchage, installation sanitaire et dépannage d'urgence. Travail soigné et garanti, devis gratuit avant chaque intervention.
          </p>
          <div className="k245">
            <span className="k246">
              Fuites d'eau
            </span>
            <span className="k246">
              Débouchage
            </span>
            <span className="k246">
              Installation sanitaire
            </span>
            <span className="k246">
              Urgence 24/7
            </span>
          </div>
        </div>
        <div className="k88">
          <h2 className="k247">
            Services proposés
          </h2>
          <div className="k248">
            <div className="k249">
              <div>
                <div className="k23">
                  Réparation de fuite d'eau
                </div>
                <div className="k250">
                  Intervention rapide · 1-2h
                </div>
              </div>
              <div className="k251">
                <span className="k252">
                  250 Gdes
                </span>
                <button className="k253" onClick={nav.reserver}>
                  Réserver
                </button>
              </div>
            </div>
            <div className="k249">
              <div>
                <div className="k23">
                  Débouchage de canalisation
                </div>
                <div className="k250">
                  Intervention rapide · 1h
                </div>
              </div>
              <div className="k251">
                <span className="k252">
                  200 Gdes
                </span>
                <button className="k253" onClick={nav.reserver}>
                  Réserver
                </button>
              </div>
            </div>
            <div className="k249">
              <div>
                <div className="k23">
                  Installation sanitaire complète
                </div>
                <div className="k250">
                  Sur devis · demi-journée
                </div>
              </div>
              <div className="k251">
                <span className="k252">
                  Sur devis
                </span>
                <button className="k253" onClick={nav.reserver}>
                  Réserver
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="k88">
          <h2 className="k247">
            Réalisations
          </h2>
          <div className="k254">
            <img className="k255" src={img("gal-1", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
            <img className="k255" src={img("gal-2", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
            <img className="k255" src={img("gal-3", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
            <img className="k255" src={img("gal-4", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
            <img className="k255" src={img("gal-5", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
            <img className="k255" src={img("gal-6", 800, 600)} alt="R\u00e9alisation" style={{objectFit: "cover", borderRadius: "14px"}} />
          </div>
        </div>
        <div className="k88">
          <div className="k256">
            <h2 className="k257">
              Avis clients ({proReviews})
            </h2>
            <div className="k258">
              <i className="icon fa-solid fa-star" style={{fontSize: "18px", color: "#F59E0B"}}></i>
              <strong className="k259">
                {proRating}
              </strong>
            </div>
          </div>
          <div className="k260">
            {(selectedProAvis || []).length === 0 ? (
<p style={{color: "#6B7280", fontSize: 14}}>Aucun avis pour le moment.</p>
) : selectedProAvis.slice(0, 5).map((a, __i) => (
<div key={a.identifiant ?? __i}>
              <div className="k20">
                <span className="k262">
                  {"★".repeat(Math.round(a.note || 0))}
                </span>
                <span className="k95">
                  {a.date || ""}
                </span>
              </div>
              <p className="k264">
                {a.commentaire}
              </p>
            </div>
))}
          </div>
        </div>
      </div>
      <aside className="k267">
        <div className="k268">
          <span className="k269">
            {pro.price || "—"}
          </span>
          <span className="k270">
            Gdes
          </span>
          <span className="k271">
            / heure
          </span>
        </div>
        <div className="k272">
          <div>
            <div className="k273">
              Date
            </div>
            <div className="k274">
              <i className="icon fa-solid fa-calendar-days" style={{fontSize: "17px", color: "#139356"}}></i>
              15 Jan 2026
            </div>
          </div>
          <div>
            <div className="k273">
              Créneau
            </div>
            <div className="k274">
              <i className="icon fa-solid fa-clock" style={{fontSize: "17px", color: "#139356"}}></i>
              14h00 – 16h00
            </div>
          </div>
        </div>
        <button className="k275" onClick={nav.reserver}>
          Réserver maintenant
        </button>
        <div className="k276">
          <i className="icon fa-solid fa-lock" style={{fontSize: "15px", color: "#9CA3AF"}}></i>
          Paiement 100% sécurisé
        </div>
      </aside>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Profil;
