import React from "react";
import { useApp } from "../AppContext.jsx";
import { img } from "../images.js";

function Accueil() {
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
  <section className="k1">
    <div className="k2">
      <div>
        <div className="k3">
          <span className="k4"></span>
          12 000+ professionnels vérifiés en Haïti
        </div>
        <h1 className="k5">
          Trouvez le bon pro,
          <br />
          <span className="k6">
            près de chez vous.
          </span>
        </h1>
        <p className="k7">
          Plomberie, électricité, ménage, jardinage… Réservez en quelques clics un professionnel qualifié et payez en toute sécurité.
        </p>
        <div className="k8">
          <div className="k9">
            <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "20px", color: "#139356"}}></i>
            <input className="k10" value={heroSearchVal} onChange={onHeroSearch} onKeyDown={onHeroKey} placeholder="Quel service cherchez-vous ?" />
          </div>
          <div className="k11"></div>
          <div className="k12">
            <i className="icon fa-solid fa-location-dot" style={{fontSize: "20px", color: "#9CA3AF"}}></i>
            <input className="k10" value={heroCityVal} onChange={onHeroCity} onKeyDown={onHeroKey} placeholder="Ville ou code postal" />
          </div>
          <button className="k13" onClick={nav.heroSearch}>
            <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "18px", color: "#fff"}}></i>
            Rechercher
          </button>
        </div>
        <div className="k14">
          <span className="k15">
            Populaire :
          </span>
          <button className="k16" onClick={nav.goCat.Plomberie}>
            Plomberie
          </button>
          <button className="k16" onClick={nav.goCat.Electricite}>
            Électricité
          </button>
          <button className="k16" onClick={nav.goCat.Menage}>
            Ménage
          </button>
          <button className="k16" onClick={nav.goCat.Climatisation}>
            Climatisation
          </button>
        </div>
      </div>
      <div className="k17">
        <img className="k18" src={img("kolabor-hero", 800, 600)} alt="Photo \u2014 pro en intervention" style={{objectFit: "cover", borderRadius: "28px"}} />
        <div className="k19">
          <div className="k20">
            <span className="k21">
              <i className="icon fa-solid fa-shield-halved" style={{fontSize: "24px", color: "#139356"}}></i>
            </span>
            <div className="k22">
              <div className="k23">
                Professionnels vérifiés
              </div>
              <div className="k24">
                Identité & compétences contrôlées
              </div>
            </div>
          </div>
          <div className="k25">
            <div>
              <div className="k26">
                12 000+
              </div>
              <div className="k27">
                Pros actifs
              </div>
            </div>
            <div className="k28">
              <div className="k29">
                4,8★
              </div>
              <div className="k27">
                Note moyenne
              </div>
            </div>
          </div>
        </div>
        <div className="k30">
          <span className="k31">
            <i className="icon fa-solid fa-lock" style={{fontSize: "20px", color: "#2563EB"}}></i>
          </span>
          <div>
            <div className="k32">
              Paiement sécurisé
            </div>
            <div className="k27">
              Protégé jusqu'au service rendu
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section className="k33">
    <div className="k34">
      <div className="k35">
        <div className="k36">
          12 000+
        </div>
        <div className="k37">
          Professionnels vérifiés
        </div>
      </div>
      <div className="k35">
        <div className="k36">
          85 000+
        </div>
        <div className="k37">
          Services réalisés
        </div>
      </div>
      <div className="k35">
        <div className="k36">
          32
        </div>
        <div className="k37">
          Villes couvertes
        </div>
      </div>
      <div className="k35">
        <div className="k38">
          4,8/5
        </div>
        <div className="k37">
          Note moyenne clients
        </div>
      </div>
    </div>
  </section>
  <section className="k39">
    <div className="k40">
      <h2 className="k41">
        Explorez par catégorie
      </h2>
      <p className="k42">
        Des centaines de services à domicile, partout en Haïti.
      </p>
    </div>
    <div className="k43">
      <button className="k44" onClick={nav.services}>
        <span className="k45">
          <i className="icon fa-solid fa-house" style={{fontSize: "24px", color: "#7C3AED"}}></i>
        </span>
        <div className="k46">
          Ménage
        </div>
        <div className="k47">
          412 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k48">
          <i className="icon fa-solid fa-shirt" style={{fontSize: "24px", color: "#0EA5E9"}}></i>
        </span>
        <div className="k46">
          Lessive & Repassage
        </div>
        <div className="k47">
          87 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k49">
          <i className="icon fa-solid fa-location-dot" style={{fontSize: "24px", color: "#19355F"}}></i>
        </span>
        <div className="k46">
          Plomberie
        </div>
        <div className="k47">
          340 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k50">
          <i className="icon fa-solid fa-bolt" style={{fontSize: "24px", color: "#F59E0B"}}></i>
        </span>
        <div className="k46">
          Électricité
        </div>
        <div className="k47">
          285 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k51">
          <i className="icon fa-solid fa-paint-roller" style={{fontSize: "24px", color: "#EC4899"}}></i>
        </span>
        <div className="k46">
          Peinture
        </div>
        <div className="k47">
          198 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k52">
          <i className="icon fa-solid fa-leaf" style={{fontSize: "24px", color: "#139356"}}></i>
        </span>
        <div className="k46">
          Jardinage
        </div>
        <div className="k47">
          156 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k50">
          <i className="icon fa-solid fa-hammer" style={{fontSize: "24px", color: "#92400E"}}></i>
        </span>
        <div className="k46">
          Menuiserie
        </div>
        <div className="k47">
          93 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k53">
          <i className="icon fa-solid fa-trowel" style={{fontSize: "24px", color: "#6B7280"}}></i>
        </span>
        <div className="k46">
          Maçonnerie
        </div>
        <div className="k47">
          71 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k49">
          <i className="icon fa-solid fa-wind" style={{fontSize: "24px", color: "#2563EB"}}></i>
        </span>
        <div className="k46">
          Climatisation
        </div>
        <div className="k47">
          124 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k54">
          <i className="icon fa-solid fa-trash" style={{fontSize: "24px", color: "#65A30D"}}></i>
        </span>
        <div className="k46">
          Débarras & Déchets
        </div>
        <div className="k47">
          58 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k52">
          <i className="icon fa-solid fa-truck" style={{fontSize: "24px", color: "#0F7A48"}}></i>
        </span>
        <div className="k46">
          Déménagement
        </div>
        <div className="k47">
          64 pros
        </div>
      </button>
      <button className="k44" onClick={nav.services}>
        <span className="k55">
          <i className="icon fa-solid fa-desktop" style={{fontSize: "24px", color: "#4F46E5"}}></i>
        </span>
        <div className="k46">
          Informatique
        </div>
        <div className="k47">
          112 pros
        </div>
      </button>
    </div>
    <div className="k56">
      <button className="k57" onClick={nav.catalogue}>
        Voir tous les services
        <i className="icon fa-solid fa-arrow-right" style={{fontSize: "18px", color: "currentColor"}}></i>
      </button>
    </div>
  </section>
  <section className="k58">
    <div className="k59">
      <div className="k60">
        <span className="k61">
          Simple et rapide
        </span>
        <h2 className="k62">
          Comment ça marche
        </h2>
      </div>
      <div className="k63">
        <div className="k35">
          <span className="k64">
            <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "28px", color: "#139356"}}></i>
            <span className="k65">
              1
            </span>
          </span>
          <h3 className="k66">
            Recherchez
          </h3>
          <p className="k67">
            Décrivez votre besoin et trouvez les pros disponibles autour de vous.
          </p>
        </div>
        <div className="k35">
          <span className="k64">
            <i className="icon fa-solid fa-calendar-days" style={{fontSize: "28px", color: "#139356"}}></i>
            <span className="k65">
              2
            </span>
          </span>
          <h3 className="k66">
            Réservez
          </h3>
          <p className="k67">
            Comparez les profils et avis, puis choisissez votre créneau idéal.
          </p>
        </div>
        <div className="k35">
          <span className="k64">
            <i className="icon fa-solid fa-check" style={{fontSize: "28px", color: "#139356"}}></i>
            <span className="k65">
              3
            </span>
          </span>
          <h3 className="k66">
            C'est réglé
          </h3>
          <p className="k67">
            Payez en sécurité et suivez votre intervention en temps réel.
          </p>
        </div>
      </div>
    </div>
  </section>
  <section className="k58">
    <div className="k68">
      <div>
        <h2 className="k41">
          Professionnels en vedette
        </h2>
        <p className="k42">
          Les mieux notés cette semaine.
        </p>
      </div>
      <button className="k69" onClick={nav.pros}>
        Voir tout
        <i className="icon fa-solid fa-arrow-right" style={{fontSize: "18px", color: "#139356"}}></i>
      </button>
    </div>
    <div className="k70">
      {featured.map((p, __i) => (
<button key={p.id ?? __i} className="k71" onClick={p.open}>
  <div className="k72">
    <img className="k73" src={img(`pcover-${p.id}`, 800, 600)} alt={p.name} style={{objectFit: "cover"}} />
    {p.popular ? (
<React.Fragment>
      <span className="k74">
        ★ Populaire
      </span>
</React.Fragment>
) : null}
  </div>
  <div className="k75">
    <span className="k76" style={{background: p.avatar}}>
      {p.initials}
    </span>
    <div className="k77">
      <span className="k46">
        {p.name}
      </span>
      <i className="icon fa-solid fa-shield-halved" style={{fontSize: "15px", color: "#22C55E"}}></i>
    </div>
    <div className="k78">
      {p.job}
    </div>
    <div className="k79">
      <i className="icon fa-solid fa-location-dot" style={{fontSize: "14px", color: "#9CA3AF"}}></i>
      {p.city}
    </div>
    <div className="k25">
      <div className="k80">
        <i className="icon fa-solid fa-star" style={{fontSize: "15px", color: "#F59E0B"}}></i>
        <span className="k32">
          {p.rating}
        </span>
        <span className="k27">
          ({p.reviews})
        </span>
      </div>
      <div className="k81">
        {p.price}
      </div>
    </div>
  </div>
</button>
))}
    </div>
  </section>
  <section className="k58">
    <div className="k82">
      <div className="k83">
        <span className="k84">
          <i className="icon fa-solid fa-shield-halved" style={{fontSize: "26px", color: "#139356"}}></i>
        </span>
        <h3 className="k66">
          Professionnels vérifiés
        </h3>
        <p className="k67">
          Identité, compétences et assurances contrôlées avant chaque inscription.
        </p>
      </div>
      <div className="k83">
        <span className="k85">
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "26px", color: "#2563EB"}}></i>
        </span>
        <h3 className="k66">
          Paiement sécurisé
        </h3>
        <p className="k67">
          Vos fonds sont protégés et libérés uniquement après le service rendu.
        </p>
      </div>
      <div className="k83">
        <span className="k86">
          <i className="icon fa-solid fa-message" style={{fontSize: "26px", color: "#F59E0B"}}></i>
        </span>
        <h3 className="k66">
          Support 7j/7
        </h3>
        <p className="k67">
          Une équipe à votre écoute pour vous accompagner à chaque étape.
        </p>
      </div>
    </div>
  </section>
  <section className="k58">
    <h2 className="k87">
      Ils nous font confiance
    </h2>
    <div className="k82">
      <div className="k88">
        <div className="k89">
          <span className="k90">
            ★★★★★
          </span>
        </div>
        <p className="k91">
          « Intervention rapide et efficace. Marc a résolu notre fuite en moins d'une heure. Je recommande vivement ! »
        </p>
        <div className="k92">
          <span className="k93">
            SM
          </span>
          <div>
            <div className="k94">
              Sophie Martin
            </div>
            <div className="k95">
              Delmas
            </div>
          </div>
        </div>
      </div>
      <div className="k88">
        <div className="k89">
          <span className="k90">
            ★★★★★
          </span>
        </div>
        <p className="k91">
          « Plateforme très simple. J'ai trouvé une aide-ménagère sérieuse en 5 minutes. Le paiement sécurisé me rassure. »
        </p>
        <div className="k92">
          <span className="k96">
            RJ
          </span>
          <div>
            <div className="k94">
              Ricardo Joseph
            </div>
            <div className="k95">
              Pétion-Ville
            </div>
          </div>
        </div>
      </div>
      <div className="k88">
        <div className="k89">
          <span className="k90">
            ★★★★★
          </span>
        </div>
        <p className="k91">
          « En tant que pro, Kolabor m'a apporté beaucoup de clients. Les paiements sont fiables et rapides. »
        </p>
        <div className="k92">
          <span className="k97">
            NJ
          </span>
          <div>
            <div className="k94">
              Naïka Joseph
            </div>
            <div className="k95">
              Électricienne · Delmas
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section className="k98">
    <div className="k99">
      <div className="k100">
        <span className="k101">
          <i className="icon fa-solid fa-mobile-screen-button" style={{fontSize: "15px", color: "#139356"}}></i>
          Application mobile
        </span>
        <h2 className="k102">
          Kolabor dans votre poche
        </h2>
        <p className="k103">
          Réservez un professionnel, suivez vos demandes et discutez en temps réel, où que vous soyez. Téléchargez l'application gratuitement.
        </p>
        <div className="k104">
          <button className="k105">
            <i className="icon fa-brands fa-apple" style={{fontSize: "22px", color: "#fff"}}></i>
            <span className="k106">
              <span className="k107">
                Télécharger sur
              </span>
              <span className="k108">
                App Store
              </span>
            </span>
          </button>
          <button className="k105">
            <i className="icon fa-brands fa-google-play" style={{fontSize: "22px"}}></i>
            <span className="k106">
              <span className="k107">
                Disponible sur
              </span>
              <span className="k108">
                Google Play
              </span>
            </span>
          </button>
        </div>
        <div className="k109">
          <span className="k110">
            ★★★★★
          </span>
          <span className="k111">
            4,8 · plus de 10 000 téléchargements
          </span>
        </div>
      </div>
      <div className="k112">
        <div className="k113">
          <div className="k114"></div>
          <div className="k115">
            <div className="k116"></div>
            <img className="k117" src={img("app-phone", 800, 600)} alt="Capture de l'app Kolabor" style={{objectFit: "cover", borderRadius: "26px"}} />
          </div>
        </div>
      </div>
    </div>
  </section>
  <section className="k118">
    <div className="k119">
      <div className="k120"></div>
      <div className="k121">
        <span className="k122">
          Espace professionnel
        </span>
        <h2 className="k123">
          Développez votre activité avec Kolabor
        </h2>
        <p className="k124">
          Recevez des demandes qualifiées, gérez votre agenda et soyez payé en toute sécurité. Inscription gratuite.
        </p>
        <div className="k125">
          <button className="k126" onClick={nav.devenirPro}>
            Devenir professionnel
          </button>
          <button className="k127" onClick={nav.prolanding}>
            En savoir plus
          </button>
        </div>
      </div>
    </div>
  </section>
    </React.Fragment>
  );
}

export default Accueil;
