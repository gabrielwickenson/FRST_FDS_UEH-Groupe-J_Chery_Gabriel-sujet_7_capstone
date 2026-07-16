import React from "react";
import { useApp } from "../AppContext.jsx";

function Signup() {
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
  <div className="k327">
    <div className="k328">
      <h1 className="k329">
        Créer un compte
      </h1>
      <p className="k305">
        Rejoignez des milliers d'utilisateurs Kolabor.
      </p>
      <div className="k330">
        <button onClick={setRoleClient} style={roleClientStyle}>
          Je suis client
        </button>
        <button onClick={setRolePro} style={roleProStyle}>
          Je suis professionnel
        </button>
      </div>
      <div className="k331">
        <div className="k332">
          <div>
            <label className="k307">
              Prénom
            </label>
            <input className="k333" placeholder="Peter" />
          </div>
          <div>
            <label className="k307">
              Nom
            </label>
            <input className="k333" placeholder="Joseph" />
          </div>
        </div>
        <div>
          <label className="k307">
            Adresse e-mail
          </label>
          <input className="k333" placeholder="vous@email.com" />
        </div>
        <div>
          <label className="k307">
            Téléphone
          </label>
          <div className="k334">
            <span className="k335">
              +509
            </span>
            <input className="k336" placeholder="55 66 7788" />
          </div>
        </div>
        {isRolePro ? (
<React.Fragment>
          <div className="k337">
            <div className="k338">
              <i className="icon fa-solid fa-shield-halved" style={{fontSize: "16px", color: "#139356"}}></i>
              Informations professionnelles
            </div>
            <div>
              <label className="k307">
                Métier / Catégorie
              </label>
              <div className="k201">
                <select className="k339">
                  {metiers.map((m, __i) => (
<option key={m.id ?? __i}>
  {m}
</option>
))}
                </select>
                <i className="icon fa-solid fa-chevron-down k340" style={{fontSize: "18px", color: "#6B7280"}}></i>
              </div>
            </div>
            <div className="k332">
              <div>
                <label className="k307">
                  Zone d'intervention
                </label>
                <div className="k201">
                  <select className="k339">
                    {zonesHaiti.map((z, __i) => (
<option key={z.id ?? __i}>
  {z}
</option>
))}
                  </select>
                  <i className="icon fa-solid fa-chevron-down k340" style={{fontSize: "18px", color: "#6B7280"}}></i>
                </div>
              </div>
              <div>
                <label className="k307">
                  Années d'expérience
                </label>
                <input className="k341" placeholder="8" />
              </div>
            </div>
            <div>
              <label className="k307">
                Tarif horaire (Gdes)
              </label>
              <input className="k341" placeholder="250" />
            </div>
            <div>
              <label className="k307">
                Pièce d'identité (vérification)
              </label>
              <div className="k342">
                <i className="icon fa-solid fa-upload" style={{fontSize: "22px", color: "#9CA3AF"}}></i>
                <span className="k343">
                  Déposer votre pièce
                </span>
                <span className="k27">
                  PNG, JPG, PDF jusqu'à 10 Mo
                </span>
              </div>
            </div>
          </div>
</React.Fragment>
) : null}
        <div>
          <label className="k307">
            Mot de passe
          </label>
          <input className="k344" type="password" placeholder="\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022" />
        </div>
        <label className="k345">
          <span className="k346">
            <i className="icon fa-solid fa-check" style={{fontSize: "13px", color: "#fff"}}></i>
          </span>
          J'accepte les
          <span className="k347">
            conditions générales
          </span>
          et la politique de confidentialité.
        </label>
        <button className="k348" onClick={nav.creerCompte}>
          Créer mon compte
        </button>
      </div>
      <p className="k349">
        Déjà un compte ?
        <span className="k318" onClick={nav.login}>
          Se connecter
        </span>
      </p>
    </div>
  </div>
  <div className="k350">
    <div className="k351">
      <div className="k320"></div>
      <div className="k352"></div>
      <div className="k321">
        <div className="k353">
          Rejoignez Kolabor
        </div>
        <div className="k354">
          Des milliers de professionnels et de clients se font confiance chaque jour.
        </div>
        <div className="k355">
          <div className="k356">
            <span className="k357">
              <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
            </span>
            Inscription 100% gratuite
          </div>
          <div className="k356">
            <span className="k357">
              <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
            </span>
            Profils vérifiés et sécurisés
          </div>
          <div className="k356">
            <span className="k357">
              <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
            </span>
            Paiement protégé à chaque réservation
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Signup;
