import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { navigateTo } from "../router.jsx";
import { uploadUserPhoto } from "../api/users.js";
import { getUserId } from "../utils/field.js";

const PIECE_MAX_SIZE = 10 * 1024 * 1024;
const PIECE_TYPES = ["image/png", "image/jpeg", "image/jpg", "application/pdf"];

function Signup() {
  const { register, login, authLoading, authError, setAuthError } = useAuth();
  const [prenom, setPrenom] = React.useState("");
  const [nomField, setNomField] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [telephone, setTelephone] = React.useState("");
  const [metier, setMetier] = React.useState("");
  const [zone, setZone] = React.useState("");
  const [tarifHoraire, setTarifHoraire] = React.useState("");
  const [motDePasse, setMotDePasse] = React.useState("");
  const [acceptTerms, setAcceptTerms] = React.useState(false);
  const [pieceFile, setPieceFile] = React.useState(null);
  const [pieceError, setPieceError] = React.useState("");
  const [submitting, setSubmitting] = React.useState(false);
  const pieceInputRef = React.useRef(null);

  const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

  function resetForm() {
    setPrenom("");
    setNomField("");
    setEmail("");
    setTelephone("");
    setMetier("");
    setZone("");
    setTarifHoraire("");
    setMotDePasse("");
    setAcceptTerms(false);
    removePiece();
  }

  function handlePieceChange(e) {
    const file = e.target.files?.[0];
    if (!file) return;
    if (!PIECE_TYPES.includes(file.type)) {
      setPieceError("Format non supporté. Utilisez un PNG, JPG ou PDF.");
      return;
    }
    if (file.size > PIECE_MAX_SIZE) {
      setPieceError("Le fichier dépasse 10 Mo.");
      return;
    }
    setPieceError("");
    setPieceFile(file);
  }

  function removePiece() {
    setPieceFile(null);
    setPieceError("");
    if (pieceInputRef.current) pieceInputRef.current.value = "";
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setAuthError("");
    if (!acceptTerms) {
      setAuthError("Merci d'accepter les conditions générales pour continuer.");
      return;
    }
    const payload = {
      nom: `${prenom} ${nomField}`.trim(),
      email,
      motDePasse,
      telephone: telephone ? `+509 ${telephone}` : undefined,
      role: isRolePro ? "PRESTATAIRE" : "CLIENT",
      ...(isRolePro ? {
        competences: metier || undefined,
        tarifHoraire: tarifHoraire ? Number(tarifHoraire) : undefined,
        zoneIntervention: zone || undefined,
      } : {}),
    };
    setSubmitting(true);
    const startedAt = Date.now();
    const settle = async () => {
      const elapsed = Date.now() - startedAt;
      if (elapsed < 2000) await wait(2000 - elapsed);
    };
    try {
      const user = await register(payload);
      if (user) {
        if (pieceFile) {
          const id = getUserId(user);
          if (id) {
            try { await uploadUserPhoto(id, pieceFile); } catch { /* upload de la pièce échoué, on n'empêche pas l'inscription */ }
          }
        }
        await settle();
        const role = (user?.["rôle"] || user?.role || "").toString().toUpperCase();
        resetForm();
        navigateTo(role.includes("PRO") ? "dashpro" : "dashclient");
        return;
      }
      // Le backend n'a pas renvoyé de session : on tente une connexion directe
      // avec les identifiants qui viennent d'être créés.
      try {
        const loggedUser = await login({ email, motDePasse });
        if (pieceFile) {
          const id = getUserId(loggedUser);
          if (id) {
            try { await uploadUserPhoto(id, pieceFile); } catch { /* upload de la pièce échoué, on n'empêche pas l'inscription */ }
          }
        }
        await settle();
        const role = (loggedUser?.["rôle"] || loggedUser?.role || "").toString().toUpperCase();
        resetForm();
        navigateTo(role.includes("PRO") ? "dashpro" : "dashclient");
      } catch {
        await settle();
        navigateTo("login");
      }
    } catch {
      await settle();
      // authError déjà renseigné par le contexte
    } finally {
      setSubmitting(false);
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
      <form className="k331" onSubmit={handleSubmit}>
        {authError ? (
          <div style={{background: "#FEE2E2", color: "#B91C1C", padding: "10px 14px", borderRadius: 10, fontSize: 13.5, fontWeight: 600}}>
            {authError}
          </div>
        ) : null}
        <div className="k332">
          <div>
            <label className="k307">
              Prénom
            </label>
            <input className="k333" placeholder="Prénom" value={prenom} onChange={(e) => setPrenom(e.target.value)} />
          </div>
          <div>
            <label className="k307">
              Nom
            </label>
            <input className="k333" placeholder="Nom" value={nomField} onChange={(e) => setNomField(e.target.value)} />
          </div>
        </div>
        <div>
          <label className="k307">
            Adresse e-mail
          </label>
          <input className="k333" type="email" required placeholder="vous@email.com" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div>
          <label className="k307">
            Téléphone
          </label>
          <div className="k334">
            <span className="k335">
              +509
            </span>
            <input className="k336" placeholder="55 66 7788" value={telephone} onChange={(e) => setTelephone(e.target.value)} />
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
                <select className="k339" value={metier} onChange={(e) => setMetier(e.target.value)}>
                  <option value="">Sélectionnez un métier</option>
                  {metiers.map((m, __i) => (
<option key={m.id ?? __i} value={m}>
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
                  <select className="k339" value={zone} onChange={(e) => setZone(e.target.value)}>
                    <option value="">Sélectionnez une zone</option>
                    {zonesHaiti.map((z, __i) => (
<option key={z.id ?? __i} value={z}>
  {z}
</option>
))}
                  </select>
                  <i className="icon fa-solid fa-chevron-down k340" style={{fontSize: "18px", color: "#6B7280"}}></i>
                </div>
              </div>
              <div>
                <label className="k307">
                  Tarif horaire (Gdes)
                </label>
                <input className="k341" placeholder="250" value={tarifHoraire} onChange={(e) => setTarifHoraire(e.target.value)} />
              </div>
            </div>
            <div>
              <label className="k307">
                Pièce d'identité (vérification)
              </label>
              <input
                ref={pieceInputRef}
                type="file"
                accept="image/png,image/jpeg,application/pdf"
                style={{display: "none"}}
                onChange={handlePieceChange}
              />
              <div className="k342" onClick={() => pieceInputRef.current?.click()} style={{cursor: "pointer"}}>
                <i className={`icon fa-solid ${pieceFile ? "fa-circle-check" : "fa-upload"}`} style={{fontSize: "22px", color: pieceFile ? "#139356" : "#9CA3AF"}}></i>
                <span className="k343">
                  {pieceFile ? pieceFile.name : "Déposer votre pièce"}
                </span>
                <span className="k27">
                  {pieceFile ? "Cliquez pour changer le fichier" : "PNG, JPG, PDF jusqu'à 10 Mo"}
                </span>
                {pieceFile ? (
                  <span
                    className="k318"
                    onClick={(e) => { e.stopPropagation(); removePiece(); }}
                  >
                    Retirer
                  </span>
                ) : null}
              </div>
              {pieceError ? (
                <div style={{color: "#B91C1C", fontSize: 13, fontWeight: 600, marginTop: 6}}>
                  {pieceError}
                </div>
              ) : null}
            </div>
          </div>
</React.Fragment>
) : null}
        <div>
          <label className="k307">
            Mot de passe
          </label>
          <input className="k344" type="password" required minLength={6} placeholder="**************" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} />
        </div>
        <label className="k345">
          <span className="k346" onClick={() => setAcceptTerms((v) => !v)} style={acceptTerms ? {background: "#139356"} : undefined}>
            <i className="icon fa-solid fa-check" style={{fontSize: "13px", color: "#fff"}}></i>
          </span>
          J'accepte les
          <span className="k347">
            conditions générales
          </span>
          et la politique de confidentialité.
        </label>
        <button className="k348" type="submit" disabled={authLoading || submitting || !acceptTerms} title={!acceptTerms ? "Veuillez accepter les conditions générales pour continuer" : undefined}>
          {submitting || authLoading ? "Envoi en cours..." : "Créer mon compte"}
        </button>
      </form>
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
  </div>
    </React.Fragment>
  );
}

export default Signup;
