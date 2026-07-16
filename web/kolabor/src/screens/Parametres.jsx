import React from "react";
import { useApp } from "../AppContext.jsx";

function Parametres() {
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
        <div className="k430" onClick={nav.dashclient}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes réservations
        </div>
        <div className="k430" onClick={nav.favoris}>
          <i className="icon fa-solid fa-heart" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes favoris
        </div>
        <div className="k430" onClick={nav.factures}>
          <i className="icon fa-solid fa-file" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Factures
        </div>
        <div className="k431">
          <i className="icon fa-solid fa-sliders" style={{fontSize: "18px", color: "#fff"}}></i>
          Paramètres
        </div>
      </div>
    </aside>
    <div className="k735">
      <h1 className="k493">
        Paramètres
      </h1>
      <p className="k494">
        Gérez votre profil et vos préférences.
      </p>
      <div className="k736">
        <h2 className="k737">
          Informations personnelles
        </h2>
        <div className="k738">
          <span className="k739">
            PJ
          </span>
          <button className="k740">
            Changer la photo
          </button>
        </div>
        <div className="k642">
          <div>
            <label className="k307">
              Prénom
            </label>
            <input className="k333" defaultValue="Peter" />
          </div>
          <div>
            <label className="k307">
              Nom
            </label>
            <input className="k333" defaultValue="Joseph" />
          </div>
        </div>
        <div className="k643">
          <label className="k307">
            Adresse e-mail
          </label>
          <input className="k333" defaultValue="peter.joseph@email.ht" />
        </div>
        <div className="k643">
          <label className="k307">
            Téléphone
          </label>
          <div className="k334">
            <span className="k335">
              +509
            </span>
            <input className="k336" defaultValue="55 66 7788" />
          </div>
        </div>
        <button className="k741">
          Enregistrer
        </button>
      </div>
      <div className="k742">
        <h2 className="k743">
          Notifications
        </h2>
        <p className="k744">
          Choisissez comment vous souhaitez être informé.
        </p>
        <div className="k745">
          <div>
            <div className="k746">
              Notifications par e-mail
            </div>
            <div className="k95">
              Confirmations et rappels de réservation
            </div>
          </div>
          <span className="k747">
            <span className="k748"></span>
          </span>
        </div>
        <div className="k749">
          <div>
            <div className="k746">
              SMS
            </div>
            <div className="k95">
              Alertes urgentes par message
            </div>
          </div>
          <span className="k750">
            <span className="k751"></span>
          </span>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Parametres;
