import React from "react";
import { useApp } from "../AppContext.jsx";

function Legal() {
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
  <section className="k651">
    <div className="k526">
      <span className="k131" onClick={nav.accueil}>
        Accueil
      </span>
      /
      <span className="k132">
        Conditions générales
      </span>
    </div>
    <h1 className="k659">
      Conditions générales d'utilisation
    </h1>
    <p className="k660">
      Dernière mise à jour : 1er janvier 2026
    </p>
    <div className="k661">
      <div>
        <h2 className="k662">
          1. Objet
        </h2>
        <p className="k663">
          Les présentes conditions régissent l'utilisation de la plateforme Kolabor, mettant en relation des particuliers avec des professionnels de services à domicile en Haïti.
        </p>
      </div>
      <div>
        <h2 className="k662">
          2. Inscription
        </h2>
        <p className="k663">
          L'inscription est gratuite. L'utilisateur s'engage à fournir des informations exactes et à maintenir la confidentialité de ses identifiants de connexion.
        </p>
      </div>
      <div>
        <h2 className="k662">
          3. Réservations et paiements
        </h2>
        <p className="k663">
          Les paiements sont traités de manière sécurisée. Les fonds sont conservés puis libérés au professionnel une fois la prestation réalisée et validée par le client.
        </p>
      </div>
      <div>
        <h2 className="k662">
          4. Responsabilités
        </h2>
        <p className="k663">
          Kolabor agit en tant qu'intermédiaire. Les professionnels sont seuls responsables de la qualité et de la conformité des prestations fournies.
        </p>
      </div>
      <div>
        <h2 className="k662">
          5. Protection des données
        </h2>
        <p className="k663">
          Vos données personnelles sont traitées conformément à notre politique de confidentialité. Elles ne sont jamais vendues à des tiers.
        </p>
      </div>
      <div>
        <h2 className="k662">
          6. Contact
        </h2>
        <p className="k663">
          Pour toute question relative à ces conditions, contactez-nous à l'adresse bonjour@kolabor.ht.
        </p>
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Legal;
