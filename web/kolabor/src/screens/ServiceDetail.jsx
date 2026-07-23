import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function ServiceDetail() {
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
    selectedService,
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
  const sv = selectedService || {};
  return (
    <React.Fragment>
  <section className="k277">
    <div className="k278">
      <span className="k131" onClick={nav.services}>
        Services
      </span>
      /  {sv.cat || ""}  /
      <span className="k132">
        {sv.title || "Service"}
      </span>
    </div>
    <div className="k279">
      <div>
        <div className="k280">
          <img className="k73" src={img(`svc-${sv.id}`, 800, 600)} alt="Photo du service" style={{objectFit: "cover", borderRadius: "24px"}} />
          <span className="k281">
            {sv.cat || ""}
          </span>
        </div>
        <div className="k282">
          <img className="k283" src={img("svc-th-1", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-2", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-3", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-4", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
        </div>
        <h1 className="k284">
          {sv.title || "Service"}
        </h1>
        <div className="k285">
          <span className="k230">
            Intervention rapide
          </span>
        </div>
        <div className="k286">
          <h2 className="k287">
            Description
          </h2>
          <p className="k244">
            {sv.description || "Aucune description disponible pour ce service."}
          </p>
          <h3 className="k288">
            Ce qui est inclus
          </h3>
          <div className="k289">
            <div className="k290">
              <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
              Diagnostic complet de la fuite
            </div>
            <div className="k290">
              <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
              Réparation ou remplacement des pièces
            </div>
            <div className="k290">
              <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
              Test d'étanchéité après intervention
            </div>
            <div className="k290">
              <i className="icon fa-solid fa-check" style={{fontSize: "18px", color: "#139356"}}></i>
              Garantie 30 jours
            </div>
          </div>
        </div>
      </div>
      <aside className="k291">
        <div className="k292">
          <div className="k293">
            Tarif
          </div>
          <div className="k294">
            <span className="k295">
              Sur devis
            </span>
          </div>
          <button className="k297" onClick={nav.reserver}>
            Réserver ce service
          </button>
          <button className="k298" onClick={nav.messages}>
            Demander un devis
          </button>
        </div>
        <div className="k299">
          <div className="k22">
            <div className="k23">
              Trouvez un professionnel
            </div>
            <div className="k95">
              Pour {sv.cat || "ce service"}
            </div>
          </div>
          <button className="k301" onClick={nav.pros}>
            Voir les professionnels
          </button>
        </div>
      </aside>
    </div>
  </section>
    </React.Fragment>
  );
}

export default ServiceDetail;
