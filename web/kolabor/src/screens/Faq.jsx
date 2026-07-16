import React from "react";
import { useApp } from "../AppContext.jsx";

function Faq() {
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
    <div className="k652">
      <div className="k526">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          FAQ
        </span>
      </div>
      <h1 className="k133">
        Questions fréquentes
      </h1>
      <p className="k596">
        Tout ce que vous devez savoir sur Kolabor.
      </p>
    </div>
    <div className="k653">
      {faqList.map((f, __i) => (
<div key={f.id ?? __i} className="k654">
  <div className="k655" onClick={f.toggle}>
    <h3 className="k446">
      {f.q}
    </h3>
    <i className="icon fa-solid fa-plus k656" style={{fontSize: "20px", color: "#139356"}}></i>
  </div>
  {f.isOpen ? (
<React.Fragment>
    <p className="k657">
      {f.a}
    </p>
</React.Fragment>
) : null}
</div>
))}
    </div>
    <div className="k658">
      <p className="k198">
        Une autre question ?
        <span className="k318" onClick={nav.contact}>
          Contactez-nous
        </span>
      </p>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Faq;
