import React from "react";
import { useApp } from "../AppContext.jsx";

function DashClient() {
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
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        <span className="k427">
          PJ
        </span>
        <div>
          <div className="k23">
            Peter Joseph
          </div>
          <div className="k428">
            Pétion-Ville
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
        <div className="k430" onClick={nav.favoris}>
          <i className="icon fa-solid fa-heart" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes favoris
        </div>
        <div className="k430" onClick={nav.messages}>
          <i className="icon fa-solid fa-message" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Messagerie
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
        Bonjour, Peter 👋
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
            2
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Terminées
          </div>
          <div className="k435">
            8
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Favoris
          </div>
          <div className="k435">
            5
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Total dépensé
          </div>
          <div className="k436">
            3 200
            <span className="k437">
              Gdes
            </span>
          </div>
        </div>
      </div>
      <div className="k438">
        <button className="k439">
          À venir
        </button>
        <button className="k440">
          En cours
        </button>
        <button className="k441">
          Terminées
        </button>
      </div>
      <div className="k442">
        <div className="k443">
          <div className="k444">
            <span className="k445"></span>
            <div className="k22">
              <div className="k190">
                <h3 className="k446">
                  Réparation de fuite d'eau
                </h3>
                <span className="k447">
                  Confirmé
                </span>
              </div>
              <div className="k448">
                avec Marc Fontaine · Plombier certifié
              </div>
              <div className="k449">
                <span className="k450">
                  <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
                  15 Jan 2026
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#139356"}}></i>
                  14h00 – 16h00
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-location-dot" style={{fontSize: "16px", color: "#139356"}}></i>
                  Pétion-Ville
                </span>
              </div>
            </div>
          </div>
          <div className="k451">
            <button className="k452">
              Voir les détails
            </button>
            <button className="k453" onClick={nav.messages}>
              Envoyer un message
            </button>
            <button className="k454">
              Annuler
            </button>
          </div>
        </div>
        <div className="k443">
          <div className="k444">
            <span className="k455"></span>
            <div className="k22">
              <div className="k190">
                <h3 className="k446">
                  Installation électrique
                </h3>
                <span className="k456">
                  En attente
                </span>
              </div>
              <div className="k448">
                avec Naïka Joseph · Électricienne
              </div>
              <div className="k449">
                <span className="k450">
                  <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
                  18 Jan 2026
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#139356"}}></i>
                  10h00 – 12h00
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-location-dot" style={{fontSize: "16px", color: "#139356"}}></i>
                  Delmas
                </span>
              </div>
            </div>
          </div>
          <div className="k451">
            <button className="k452">
              Voir les détails
            </button>
            <button className="k453" onClick={nav.messages}>
              Envoyer un message
            </button>
            <button className="k454">
              Annuler
            </button>
          </div>
        </div>
        <div className="k457">
          <div className="k444">
            <span className="k458"></span>
            <div className="k22">
              <div className="k190">
                <h3 className="k446">
                  Nettoyage complet maison
                </h3>
                <span className="k459">
                  Terminé
                </span>
              </div>
              <div className="k448">
                avec Roselène Pierre · Aide-ménagère
              </div>
              <div className="k449">
                <span className="k450">
                  <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#9CA3AF"}}></i>
                  9 Jan 2026
                </span>
                <span className="k450">
                  <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#9CA3AF"}}></i>
                  08h00 – 10h00
                </span>
              </div>
            </div>
          </div>
          <div className="k451">
            <button className="k460">
              <i className="icon fa-solid fa-star" style={{fontSize: "15px", color: "#fff"}}></i>
              Laisser un avis
            </button>
            <button className="k453" onClick={nav.pros}>
              Réserver à nouveau
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default DashClient;
