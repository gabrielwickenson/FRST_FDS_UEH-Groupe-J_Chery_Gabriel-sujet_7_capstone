import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Blog() {
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
  <section className="k595">
    <div className="k526">
      <span className="k131" onClick={nav.accueil}>
        Accueil
      </span>
      /
      <span className="k132">
        Blog
      </span>
    </div>
    <h1 className="k133">
      Le blog Kolabor
    </h1>
    <p className="k596">
      Conseils, guides et astuces pour votre maison.
    </p>
  </section>
  <section className="k597">
    <div className="k598">
      <div className="k599">
        <img className="k600" src={img("blog-feat", 800, 600)} alt="Image article \u00e0 la une" style={{objectFit: "cover"}} />
        <div className="k601">
          <span className="k602">
            Guide
          </span>
          <h2 className="k603">
            Comment choisir le bon plombier en Haïti
          </h2>
          <p className="k604">
            Nos conseils pour identifier un professionnel fiable, comparer les devis et éviter les mauvaises surprises.
          </p>
          <div className="k605">
            15 janvier 2026 · 5 min de lecture
          </div>
        </div>
      </div>
      <div className="k158">
        <div className="k606">
          <img className="k607" src={img("blog-2", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
          <div className="k608">
            <span className="k609">
              Entretien
            </span>
            <h3 className="k610">
              5 gestes pour entretenir votre climatiseur
            </h3>
            <div className="k611">
              10 janvier 2026
            </div>
          </div>
        </div>
        <div className="k606">
          <img className="k607" src={img("blog-3", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
          <div className="k608">
            <span className="k612">
              Maison
            </span>
            <h3 className="k610">
              Préparer sa maison pour la saison des pluies
            </h3>
            <div className="k611">
              4 janvier 2026
            </div>
          </div>
        </div>
        <div className="k606">
          <img className="k607" src={img("blog-4", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
          <div className="k608">
            <span className="k613">
              Sécurité
            </span>
            <h3 className="k610">
              Installer une caméra de surveillance chez soi
            </h3>
            <div className="k611">
              28 décembre 2025
            </div>
          </div>
        </div>
      </div>
    </div>
    <div className="k614">
      <div className="k615">
        <img className="k616" src={img("blog-5", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
        <div className="k617">
          <span className="k609">
            Jardinage
          </span>
          <h3 className="k618">
            Créer un jardin tropical facile à entretenir
          </h3>
          <div className="k619">
            20 décembre 2025
          </div>
        </div>
      </div>
      <div className="k615">
        <img className="k616" src={img("blog-6", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
        <div className="k617">
          <span className="k620">
            Rénovation
          </span>
          <h3 className="k618">
            Rénover sa cuisine avec un petit budget
          </h3>
          <div className="k619">
            12 décembre 2025
          </div>
        </div>
      </div>
      <div className="k615">
        <img className="k616" src={img("blog-7", 800, 600)} alt="Article" style={{objectFit: "cover"}} />
        <div className="k617">
          <span className="k621">
            Peinture
          </span>
          <h3 className="k618">
            Bien choisir ses couleurs de peinture
          </h3>
          <div className="k619">
            5 décembre 2025
          </div>
        </div>
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Blog;
