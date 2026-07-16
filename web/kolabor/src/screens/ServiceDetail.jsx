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
  <section className="k277">
    <div className="k278">
      <span className="k131" onClick={nav.services}>
        Services
      </span>
      /  Plomberie  /
      <span className="k132">
        Réparation de fuite d'eau
      </span>
    </div>
    <div className="k279">
      <div>
        <div className="k280">
          <img className="k73" src={img("svc-detail-main", 800, 600)} alt="Photo du service" style={{objectFit: "cover", borderRadius: "24px"}} />
          <span className="k281">
            Plomberie
          </span>
        </div>
        <div className="k282">
          <img className="k283" src={img("svc-th-1", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-2", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-3", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
          <img className="k283" src={img("svc-th-4", 800, 600)} alt="Photo" style={{objectFit: "cover", borderRadius: "12px"}} />
        </div>
        <h1 className="k284">
          Réparation de fuite d'eau
        </h1>
        <div className="k285">
          <div className="k234">
            <i className="icon fa-solid fa-star" style={{fontSize: "16px", color: "#F59E0B"}}></i>
            <strong className="k132">
              4,9
            </strong>
            <span className="k211">
              (127 avis)
            </span>
          </div>
          <span className="k230">
            Intervention rapide
          </span>
        </div>
        <div className="k286">
          <h2 className="k287">
            Description
          </h2>
          <p className="k244">
            Détection et réparation de fuites d'eau sur tuyauterie, robinetterie et raccords. Diagnostic précis, intervention soignée et garantie 30 jours. Déplacement inclus dans la zone métropolitaine de Port-au-Prince.
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
            À partir de
          </div>
          <div className="k294">
            <span className="k295">
              250
            </span>
            <span className="k296">
              Gdes
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
          <div className="k20">
            <span className="k300">
              MF
            </span>
            <div className="k22">
              <div className="k23">
                Marc Fontaine
              </div>
              <div className="k95">
                Plombier certifié
              </div>
            </div>
          </div>
          <button className="k301" onClick={nav.profil}>
            Voir le profil
          </button>
        </div>
      </aside>
    </div>
  </section>
    </React.Fragment>
  );
}

export default ServiceDetail;
