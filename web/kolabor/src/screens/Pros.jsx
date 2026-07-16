import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Pros() {
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
        <div className="k188">
          <span className="k189">
            Disponible maintenant
          </span>
          <span onClick={toggleAvail} style={availTrackStyle}>
            <span style={availKnobStyle}></span>
          </span>
        </div>
        <div className="k190">
          <span className="k189">
            Vérifié uniquement
          </span>
          <span onClick={toggleVerified} style={verifTrackStyle}>
            <span style={verifKnobStyle}></span>
          </span>
        </div>
      </div>
      <div className="k184">
        <div className="k191">
          Tarif horaire
        </div>
        <div className="k192">
          <span className="k193"></span>
          <span className="k194"></span>
          <span className="k195"></span>
        </div>
        <div className="k196">
          <span>
            150 Gdes
          </span>
          <span>
            500 Gdes
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
        {filteredPros.map((p, __i) => (
<div key={p.id ?? __i} className="k144">
  <div className="k205">
    <img className="k73" src={img(`pcover-${p.id}`, 800, 600)} alt={p.name} style={{objectFit: "cover"}} />
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
    <span className="k209" style={{background: p.avatar}}>
      {p.initials}
    </span>
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
      <button className="k213" onClick={p.open}>
        Voir le profil
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
    </React.Fragment>
  );
}

export default Pros;
