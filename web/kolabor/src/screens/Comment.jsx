import React from "react";
import { useApp } from "../AppContext.jsx";

function Comment() {
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
  <section className="k153">
    <div className="k154">
      <span className="k61">
        Guide
      </span>
      <h1 className="k155">
        Comment fonctionne Kolabor
      </h1>
      <p className="k156">
        Réserver un professionnel de confiance n'a jamais été aussi simple. Voici les 4 étapes.
      </p>
    </div>
  </section>
  <section className="k157">
    <div className="k158">
      <div className="k159">
        <span className="k160">
          1
        </span>
        <div>
          <h3 className="k161">
            Décrivez votre besoin
          </h3>
          <p className="k162">
            Indiquez le service recherché et votre localisation. Notre moteur affiche instantanément les professionnels disponibles autour de vous.
          </p>
        </div>
      </div>
      <div className="k159">
        <span className="k163">
          2
        </span>
        <div>
          <h3 className="k161">
            Comparez et choisissez
          </h3>
          <p className="k162">
            Consultez les profils, avis clients, tarifs et disponibilités. Sélectionnez le pro qui vous convient et le créneau idéal.
          </p>
        </div>
      </div>
      <div className="k159">
        <span className="k164">
          3
        </span>
        <div>
          <h3 className="k161">
            Réservez et payez en sécurité
          </h3>
          <p className="k162">
            Confirmez votre réservation et réglez via la plateforme. Vos fonds sont protégés et libérés une fois le service rendu.
          </p>
        </div>
      </div>
      <div className="k159">
        <span className="k165">
          4
        </span>
        <div>
          <h3 className="k161">
            Évaluez la prestation
          </h3>
          <p className="k162">
            Après l'intervention, laissez un avis pour aider la communauté et valoriser les meilleurs professionnels.
          </p>
        </div>
      </div>
    </div>
    <div className="k166">
      <button className="k167" onClick={nav.pros}>
        Trouver un professionnel
      </button>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Comment;
