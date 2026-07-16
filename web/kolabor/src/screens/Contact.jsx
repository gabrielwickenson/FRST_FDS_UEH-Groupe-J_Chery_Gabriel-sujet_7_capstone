import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Contact() {
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
  <section className="k638">
    <div className="k639">
      <div className="k526">
        <span className="k131" onClick={nav.accueil}>
          Accueil
        </span>
        /
        <span className="k132">
          Contact
        </span>
      </div>
      <h1 className="k133">
        Contactez-nous
      </h1>
      <p className="k596">
        Une question, une suggestion ? Nous sommes là pour vous.
      </p>
    </div>
    <div className="k640">
      <div className="k641">
        <div className="k642">
          <div>
            <label className="k307">
              Nom complet
            </label>
            <input className="k333" placeholder="Peter Joseph" />
          </div>
          <div>
            <label className="k307">
              E-mail
            </label>
            <input className="k333" placeholder="vous@email.com" />
          </div>
        </div>
        <div className="k643">
          <label className="k307">
            Sujet
          </label>
          <input className="k333" placeholder="Comment pouvons-nous aider ?" />
        </div>
        <div className="k643">
          <label className="k307">
            Message
          </label>
          <textarea className="k644" placeholder="Votre message\u2026"></textarea>
        </div>
        <button className="k645">
          Envoyer le message
        </button>
      </div>
      <div className="k406">
        <div className="k646">
          <span className="k647">
            <i className="icon fa-solid fa-phone" style={{fontSize: "22px", color: "#139356"}}></i>
          </span>
          <div>
            <div className="k15">
              Téléphone
            </div>
            <div className="k374">
              +509 55 66 7788
            </div>
          </div>
        </div>
        <div className="k646">
          <span className="k648">
            <i className="icon fa-solid fa-envelope" style={{fontSize: "22px", color: "#2563EB"}}></i>
          </span>
          <div>
            <div className="k15">
              E-mail
            </div>
            <div className="k374">
              bonjour@kolabor.ht
            </div>
          </div>
        </div>
        <div className="k646">
          <span className="k649">
            <i className="icon fa-solid fa-location-dot" style={{fontSize: "22px", color: "#F59E0B"}}></i>
          </span>
          <div>
            <div className="k15">
              Adresse
            </div>
            <div className="k374">
              Pétion-Ville, Haïti
            </div>
          </div>
        </div>
        <img className="k650" src={img("contact-map", 800, 600)} alt="Carte / localisation" style={{objectFit: "cover", borderRadius: "18px"}} />
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Contact;
