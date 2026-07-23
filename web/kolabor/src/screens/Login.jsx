import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { navigateTo } from "../router.jsx";

function Login() {
  const { login, authLoading, authError, setAuthError } = useAuth();
  const [email, setEmail] = React.useState("");
  const [motDePasse, setMotDePasse] = React.useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setAuthError("");
    try {
      const user = await login({ email, motDePasse });
      const role = (user?.["rôle"] || user?.role || "").toString().toUpperCase();
      if (role.includes("ADMIN")) navigateTo("admin");
      else if (role.includes("PRO")) navigateTo("dashpro");
      else navigateTo("dashclient");
    } catch {
      // authError est déjà renseigné par le contexte
    }
  }

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
  <div className="auth-shell">
  <div className="k302">
    <div className="k303">
      <h1 className="k304">
        Bon retour 👋
      </h1>
      <p className="k305">
        Connectez-vous pour gérer vos réservations.
      </p>
      <form className="k306" onSubmit={handleSubmit}>
        {authError ? (
          <div style={{background: "#FEE2E2", color: "#B91C1C", padding: "10px 14px", borderRadius: 10, fontSize: 13.5, fontWeight: 600}}>
            {authError}
          </div>
        ) : null}
        <div>
          <label className="k307">
            Adresse e-mail
          </label>
          <input className="k308" type="email" required placeholder="vous@email.com" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div>
          <div className="k309">
            <label className="k310">
              Mot de passe
            </label>
            <span className="k311">
              Oublié ?
            </span>
          </div>
          <input className="k308" type="password" required placeholder="••••••••" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} />
        </div>
        <label className="k186">
          <span className="k312">
            <i className="icon fa-solid fa-check" style={{fontSize: "13px", color: "#fff"}}></i>
          </span>
          Se souvenir de moi
        </label>
        <button className="k313" type="submit" disabled={authLoading}>
          {authLoading ? "Connexion..." : "Se connecter"}
        </button>
        <div className="k314">
          <span className="k315"></span>
          ou
          <span className="k315"></span>
        </div>
        <button className="k316" type="button" disabled>
          <i className="icon fa-brands fa-google" style={{fontSize: "18px"}}></i>
          Continuer avec Google
        </button>
      </form>
      <p className="k317">
        Pas encore de compte ?
        <span className="k318" onClick={nav.signup}>
          S'inscrire
        </span>
      </p>
    </div>
  </div>
  <div className="k319">
    <div className="k320"></div>
    <div className="k321">
      <div className="k322">
        « Kolabor m'a permis de trouver un plombier fiable en quelques minutes. »
      </div>
      <div className="k323">
        <span className="k324">
          SM
        </span>
        <div>
          <div className="k325">
            Sophie Martin
          </div>
          <div className="k326">
            Cliente · Delmas
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
    </React.Fragment>
  );
}

export default Login;
