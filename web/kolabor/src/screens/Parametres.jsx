import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";

function Parametres() {
  const { isPro } = useAuth();
  const fileInputRef = React.useRef(null);
  const [saveMsg, setSaveMsg] = React.useState("");
  const [nomInput, setNomInput] = React.useState("");
  const [telephoneInput, setTelephoneInput] = React.useState("");
  const [bioInput, setBioInput] = React.useState("");

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
    me,
    uploadPhotoMutation,
    updateProfileMutation,
  } = useApp();

  React.useEffect(() => {
    setNomInput(me.nom);
    setTelephoneInput(me.telephone);
    setBioInput(me.bio || "");
  }, [me.nom, me.telephone, me.bio]);

  function handlePhotoChange(e) {
    const file = e.target.files?.[0];
    if (file) uploadPhotoMutation.mutate(file);
  }

  function handleEnregistrer() {
    setSaveMsg("");
    // Le tarif horaire n'est plus modifiable depuis Paramètres : il n'est
    // saisi qu'une seule fois, à l'inscription. On ne l'inclut donc jamais
    // dans cette mise à jour. Le "bio" (À propos) n'a de sens que pour un
    // compte pro : on ne l'envoie que dans ce cas.
    const payload = {
      nom: nomInput || undefined,
      telephone: telephoneInput || undefined,
      ...(isPro ? { bio: bioInput } : {}),
    };
    updateProfileMutation.mutate(payload, {
      onSuccess: () => setSaveMsg("Profil mis à jour."),
      onError: (err) => {
        // Affiche la vraie raison renvoyée par le backend (403 mauvais
        // compte, 404 profil prestataire introuvable, etc.) plutôt qu'un
        // message générique qui ne permet pas de diagnostiquer le problème.
        const status = err?.response?.status;
        const data = err?.response?.data;
        let msg = null;
        if (typeof data === "string" && data.trim()) msg = data.trim();
        else if (data && typeof data === "object") {
          if (typeof data.error === "string") msg = data.error;
          else if (typeof data.message === "string") msg = data.message;
        }
        if (!msg) msg = status ? `Erreur ${status} du serveur.` : (err?.message || "Impossible de contacter le serveur.");
        setSaveMsg(`Impossible de mettre à jour le profil : ${msg}`);
      },
    });
  }

  return (
    <React.Fragment>
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        {me.photoUrl ? (
<img src={me.photoUrl} alt="" className="k427" style={{objectFit: "cover"}} />
) : (
<span className="k427">
          {me.initials}
        </span>
)}
        <div>
          <div className="k23">
            {me.nom}
          </div>
          <div className="k428">
            {me.ville}
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k430" onClick={isPro ? nav.dashpro : nav.dashclient}>
          <i className={`icon fa-solid ${isPro ? "fa-building" : "fa-calendar-days"}`} style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          {isPro ? "Tableau de bord" : "Mes réservations"}
        </div>
        {isPro ? (
<React.Fragment>
        <div className="k430" onClick={nav.messervices}>
          <i className="icon fa-solid fa-table-columns" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes services
        </div>
        <div className="k430" onClick={nav.dispos}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Disponibilités
        </div>
        <div className="k430" onClick={nav.revenus}>
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Revenus
        </div>
</React.Fragment>
) : (
<React.Fragment>
        <div className="k430" onClick={nav.factures}>
          <i className="icon fa-solid fa-file" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Factures
        </div>
</React.Fragment>
)}
        <div className="k431">
          <i className="icon fa-solid fa-sliders" style={{fontSize: "18px", color: "#fff"}}></i>
          Paramètres
        </div>
      </div>
    </aside>
    <div className="k735">
      <h1 className="k493">
        Paramètres
      </h1>
      <p className="k494">
        Gérez votre profil et vos préférences.
      </p>
      <div className="k736">
        <h2 className="k737">
          Informations personnelles
        </h2>
        <div className="k738">
          {me.photoUrl ? (
<img src={me.photoUrl} alt="Photo de profil" className="k739" style={{objectFit: "cover"}} />
) : (
<span className="k739">
            {me.initials}
          </span>
)}
          <input type="file" accept="image/*" ref={fileInputRef} onChange={handlePhotoChange} style={{display: "none"}} />
          <button className="k740" onClick={() => fileInputRef.current?.click()} disabled={uploadPhotoMutation.isPending}>
            {uploadPhotoMutation.isPending ? "Envoi..." : "Changer la photo"}
          </button>
        </div>
        <div className="k643">
          <label className="k307">
            Nom complet
          </label>
          <input className="k333" value={nomInput} onChange={(e) => setNomInput(e.target.value)} />
        </div>
        <div className="k643">
          <label className="k307">
            Adresse e-mail
          </label>
          <input className="k333" defaultValue={me.email} disabled />
        </div>
        <div className="k643">
          <label className="k307">
            Téléphone
          </label>
          <div className="k334">
            <input className="k336" value={telephoneInput} onChange={(e) => setTelephoneInput(e.target.value)} />
          </div>
        </div>
        {isPro ? (
<div className="k643">
  <label className="k307">
    À propos
  </label>
  <textarea
    className="k386"
    placeholder="Présentez votre expérience, vos spécialités..."
    value={bioInput}
    onChange={(e) => setBioInput(e.target.value)}
    style={{width: "100%", minHeight: 110}}
  ></textarea>
</div>
) : null}
        {saveMsg ? (
<p style={{color: saveMsg === "Profil mis à jour." ? "#139356" : "#B45309", fontSize: 13.5}}>{saveMsg}</p>
) : null}
        <button className="k741" onClick={handleEnregistrer} disabled={updateProfileMutation.isPending}>
          {updateProfileMutation.isPending ? "Enregistrement..." : "Enregistrer"}
        </button>
      </div>
      <div className="k742">
        <h2 className="k743">
          Notifications
        </h2>
        <p className="k744">
          Choisissez comment vous souhaitez être informé.
        </p>
        <div className="k745">
          <div>
            <div className="k746">
              Notifications par e-mail
            </div>
            <div className="k95">
              Confirmations et rappels de réservation
            </div>
          </div>
          <span className="k747">
            <span className="k748"></span>
          </span>
        </div>
        <div className="k749">
          <div>
            <div className="k746">
              SMS
            </div>
            <div className="k95">
              Alertes urgentes par message
            </div>
          </div>
          <span className="k750">
            <span className="k751"></span>
          </span>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Parametres;
