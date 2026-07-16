import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function ProLanding() {
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
  <section className="k533">
    <div className="k534"></div>
    <div className="k535">
      <div>
        <span className="k122">
          Espace professionnel
        </span>
        <h1 className="k536">
          Développez votre activité avec Kolabor
        </h1>
        <p className="k537">
          Rejoignez des milliers de professionnels qui trouvent de nouveaux clients chaque jour. Recevez des demandes qualifiées, gérez votre agenda et soyez payé en toute sécurité.
        </p>
        <div className="k538">
          <button className="k539" onClick={nav.devenirPro}>
            Créer mon profil pro
          </button>
          <button className="k540" onClick={nav.comment}>
            Comment ça marche
          </button>
        </div>
        <div className="k541">
          <div>
            <div className="k542">
              0 Gdes
            </div>
            <div className="k543">
              Inscription gratuite
            </div>
          </div>
          <div>
            <div className="k542">
              12 000+
            </div>
            <div className="k543">
              Pros actifs
            </div>
          </div>
          <div>
            <div className="k544">
              85 000+
            </div>
            <div className="k543">
              Demandes / an
            </div>
          </div>
        </div>
      </div>
      <div className="k545">
        <img className="k546" src={img("pro-hero", 800, 600)} alt="Photo \u2014 pro au travail" style={{objectFit: "cover", borderRadius: "24px"}} />
        <div className="k547">
          <div className="k15">
            Revenus ce mois
          </div>
          <div className="k548">
            18 400 Gdes
          </div>
          <div className="k549">
            ▲ 12% vs mois dernier
          </div>
        </div>
      </div>
    </div>
  </section>
  <section className="k39">
    <div className="k550">
      <h2 className="k41">
        Pourquoi rejoindre Kolabor ?
      </h2>
      <p className="k551">
        Tout ce qu'il vous faut pour développer votre activité, au même endroit.
      </p>
    </div>
    <div className="k82">
      <div className="k552">
        <span className="k84">
          <i className="icon fa-solid fa-heart-pulse" style={{fontSize: "26px", color: "#139356"}}></i>
        </span>
        <h3 className="k66">
          Des demandes qualifiées
        </h3>
        <p className="k67">
          Recevez des demandes de clients proches de chez vous, prêts à réserver.
        </p>
      </div>
      <div className="k552">
        <span className="k85">
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "26px", color: "#2563EB"}}></i>
        </span>
        <h3 className="k66">
          Agenda en main
        </h3>
        <p className="k67">
          Gérez vos disponibilités et vos réservations depuis un tableau de bord clair.
        </p>
      </div>
      <div className="k552">
        <span className="k86">
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "26px", color: "#F59E0B"}}></i>
        </span>
        <h3 className="k66">
          Paiement garanti
        </h3>
        <p className="k67">
          Soyez payé à temps et en sécurité après chaque intervention réalisée.
        </p>
      </div>
    </div>
  </section>
  <section className="k553">
    <div className="k59">
      <h2 className="k554">
        Commencez en 3 étapes
      </h2>
      <div className="k63">
        <div className="k35">
          <span className="k555">
            1
          </span>
          <h3 className="k556">
            Créez votre profil
          </h3>
          <p className="k67">
            Renseignez votre métier, votre zone et vos tarifs. Ajoutez vos réalisations.
          </p>
        </div>
        <div className="k35">
          <span className="k557">
            2
          </span>
          <h3 className="k556">
            Recevez des demandes
          </h3>
          <p className="k67">
            Les clients vous contactent. Répondez et envoyez vos devis en quelques clics.
          </p>
        </div>
        <div className="k35">
          <span className="k558">
            3
          </span>
          <h3 className="k556">
            Travaillez & soyez payé
          </h3>
          <p className="k67">
            Réalisez la prestation et recevez votre paiement en toute sécurité.
          </p>
        </div>
      </div>
    </div>
  </section>
  <section className="k559">
    <div className="k560">
      <div className="k561">
        ★★★★★
      </div>
      <p className="k562">
        « En tant que pro, Kolabor m'a apporté beaucoup de clients. Les paiements sont fiables et rapides, et je gère tout depuis mon téléphone. »
      </p>
      <div className="k563">
        <span className="k564">
          NJ
        </span>
        <div className="k565">
          <div className="k23">
            Naïka Joseph
          </div>
          <div className="k95">
            Électricienne · Delmas
          </div>
        </div>
      </div>
    </div>
  </section>
  <section className="k118">
    <div className="k566">
      <div className="k567"></div>
      <div className="k201">
        <h2 className="k568">
          Prêt à développer votre activité ?
        </h2>
        <p className="k569">
          Inscription gratuite, sans engagement. Vos premiers clients vous attendent.
        </p>
        <button className="k570" onClick={nav.devenirPro}>
          Devenir professionnel Kolabor
        </button>
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default ProLanding;
