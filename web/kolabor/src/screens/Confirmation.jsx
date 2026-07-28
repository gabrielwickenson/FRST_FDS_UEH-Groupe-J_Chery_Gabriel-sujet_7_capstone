import React from "react";
import { useApp } from "../AppContext.jsx";

function Confirmation() {
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
    selectedProId,
    selectedService,
    selectedReservationId,
    checkoutSummary,
  } = useApp();
  const proInfo = allPros.find((p) => String(p.id) === String(selectedProId));
  const isMulti = (checkoutSummary || []).length > 1;

  const stepper = (
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
        <span className="k360">
          <i className="icon fa-solid fa-check" style={{fontSize: "15px", color: "#fff"}}></i>
        </span>
        <span className="k361">
          Date & heure
        </span>
      </div>
      <span className="k362"></span>
      <div className="k199">
        <span className="k360">
          <i className="icon fa-solid fa-check" style={{fontSize: "15px", color: "#fff"}}></i>
        </span>
        <span className="k361">
          Paiement
        </span>
      </div>
      <span className="k362"></span>
      <div className="k199">
        <span className="k360">
          <i className="icon fa-solid fa-check" style={{fontSize: "15px", color: "#fff"}}></i>
        </span>
        <span className="k361">
          Confirmation
        </span>
      </div>
    </div>
  );

  return (
    <React.Fragment>
  <div className="k358">
    {stepper}
  <div className="k411" style={{margin: "0 auto"}}>
    <span className="k412">
      <span className="k413">
        <i className="icon fa-solid fa-check" style={{fontSize: "32px", color: "#fff"}}></i>
      </span>
    </span>
    <h1 className="k229">
      {isMulti ? `${checkoutSummary.length} réservations confirmées !` : "Réservation confirmée !"}
    </h1>
    {isMulti ? (
<p className="k414">
      Vos {checkoutSummary.length} réservations sont confirmées. Un e-mail de confirmation vous a été envoyé.
    </p>
) : (
<p className="k414">
      Votre réservation avec
      <strong className="k132">
        {proInfo?.name || "le professionnel"}
      </strong>
      est confirmée. Un e-mail de confirmation vous a été envoyé.
    </p>
)}
    <div className="k415">
      {isMulti ? (
<div className="k248">
          {checkoutSummary.map((item) => (
            <div className="k249" key={item.reservationId ?? `${item.serviceTitle}-${item.proName}`}>
              <div>
                <div className="k23">
                  {item.serviceTitle}
                </div>
                <div className="k250">
                  {item.proName} {item.reservationId ? `· #${item.reservationId}` : ""}
                </div>
              </div>
              <span className="k252">
                {item.montant ? `${item.montant} Gdes` : "Sur devis"}
              </span>
            </div>
          ))}
        </div>
) : (
<React.Fragment>
      <div className="k416">
        <span className="k15">
          N° de réservation
        </span>
        <span className="k417">
          {selectedReservationId ? `#${selectedReservationId}` : "—"}
        </span>
      </div>
      <div className="k418">
        <div className="k20">
          <span className="k96">
            {proInfo?.initials || "PR"}
          </span>
          <div>
            <div className="k23">
              {proInfo?.name || "Professionnel"}
            </div>
            <div className="k95">
              {selectedService?.title || "Service"}
            </div>
          </div>
        </div>
      </div>
</React.Fragment>
)}
    </div>
    <div className="k421">
      <button className="k422" onClick={nav.dashclient}>
        Voir mes réservations
      </button>
      <button className="k423" onClick={nav.accueil}>
        Retour à l'accueil
      </button>
    </div>
  </div>
  <div style={{marginTop: 48}}>
    {stepper}
  </div>
  </div>
    </React.Fragment>
  );
}

export default Confirmation;
