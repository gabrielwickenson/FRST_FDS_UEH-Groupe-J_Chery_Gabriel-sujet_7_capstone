import React from "react";
import { useApp } from "../AppContext.jsx";

function Reserver() {
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
  } = useApp();
  return (
    <React.Fragment>
  <div className="k358">
    <div className="k359">
      <div className="k199">
        <span className="k360">
          <i className="icon fa-solid fa-check" style={{fontSize: "15px", color: "#fff"}}></i>
        </span>
        <span className="k361">
          Détails
        </span>
      </div>
      <span className="k362"></span>
      <div className="k199">
        <span className="k363">
          2
        </span>
        <span className="k364">
          Date & heure
        </span>
      </div>
      <span className="k365"></span>
      <div className="k199">
        <span className="k366">
          3
        </span>
        <span className="k367">
          Paiement
        </span>
      </div>
      <span className="k365"></span>
      <div className="k199">
        <span className="k366">
          4
        </span>
        <span className="k367">
          Confirmation
        </span>
      </div>
    </div>
    <div className="k368">
      <div className="k369">
        <div className="k370">
          <div className="k256">
            <h2 className="k371">
              Choisissez une date
            </h2>
            <div className="k372">
              <button className="k373">
                ‹
              </button>
              <span className="k374">
                Janvier 2026
              </span>
              <button className="k375">
                ›
              </button>
            </div>
          </div>
          <div className="k376">
            <span className="k377">
              L
            </span>
            <span className="k377">
              M
            </span>
            <span className="k377">
              M
            </span>
            <span className="k377">
              J
            </span>
            <span className="k377">
              V
            </span>
            <span className="k377">
              S
            </span>
            <span className="k377">
              D
            </span>
          </div>
          <div className="k378">
            {calDays.map((d, __i) => (
<div key={d.id ?? __i} style={d.style}>
  {d.label}
</div>
))}
          </div>
        </div>
        <div className="k370">
          <h2 className="k379">
            Créneaux disponibles
          </h2>
          <div className="k380">
            <button className="k381">
              08h – 10h
            </button>
            <button className="k381">
              10h – 12h
            </button>
            <button className="k382">
              14h – 16h
            </button>
            <button className="k381">
              16h – 18h
            </button>
            <button className="k383" disabled="">
              18h – 20h
            </button>
            <button className="k381">
              20h – 22h
            </button>
          </div>
        </div>
        <div className="k370">
          <h2 className="k384">
            Adresse d'intervention
          </h2>
          <input className="k385" placeholder="Rue, num\u00e9ro, quartier" defaultValue="12, Rue Pinchinat, P\u00e9tion-Ville" />
          <textarea className="k386" placeholder="Pr\u00e9cisions pour le professionnel (optionnel)"></textarea>
        </div>
      </div>
      <aside className="k267">
        <h3 className="k387">
          Récapitulatif
        </h3>
        <div className="k388">
          <span className="k389">
            MF
          </span>
          <div>
            <div className="k32">
              Marc Fontaine
            </div>
            <div className="k95">
              Réparation de fuite d'eau
            </div>
          </div>
        </div>
        <div className="k390">
          <div className="k391">
            <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
            15 Janvier 2026
          </div>
          <div className="k391">
            <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#139356"}}></i>
            14h00 – 16h00
          </div>
        </div>
        <div className="k392">
          <div className="k393">
            <span>
              Service
            </span>
            <span className="k394">
              250 Gdes
            </span>
          </div>
          <div className="k393">
            <span>
              Déplacement
            </span>
            <span className="k394">
              100 Gdes
            </span>
          </div>
          <div className="k393">
            <span>
              TVA (20%)
            </span>
            <span className="k394">
              50 Gdes
            </span>
          </div>
        </div>
        <div className="k395">
          <span className="k26">
            Total
          </span>
          <span className="k396">
            400 Gdes
          </span>
        </div>
        <button className="k275" onClick={nav.paiement}>
          Continuer vers le paiement
        </button>
      </aside>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Reserver;
