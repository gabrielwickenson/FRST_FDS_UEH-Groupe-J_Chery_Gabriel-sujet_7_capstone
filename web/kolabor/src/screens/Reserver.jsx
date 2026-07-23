import React from "react";
import { useMutation } from "@tanstack/react-query";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { createReservation } from "../api/reservations.js";
import { navigateTo } from "../router.jsx";

function Reserver() {
  const { userId } = useAuth();
  const [resDate, setResDate] = React.useState("");
  const [resTime, setResTime] = React.useState("");
  const [adresse, setAdresse] = React.useState("12, Rue Pinchinat, Pétion-Ville");
  const [notes, setNotes] = React.useState("");
  const [formError, setFormError] = React.useState("");

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
    selectedServiceId,
    selectedService,
    setSelectedReservationId,
    setSelectedReservationMontant,
  } = useApp();

  const proInfo = allPros.find((p) => String(p.id) === String(selectedProId));
  const montant = proInfo?.priceNum || 0;

  const createReservationMutation = useMutation({
    mutationFn: createReservation,
  });

  async function handleContinuer() {
    setFormError("");
    if (!selectedProId) {
      setFormError("Aucun professionnel sélectionné. Revenez à la recherche pour en choisir un.");
      return;
    }
    if (!selectedServiceId) {
      setFormError("Veuillez d'abord choisir un service depuis la page Services.");
      return;
    }
    if (!resDate || !resTime) {
      setFormError("Veuillez choisir une date et une heure.");
      return;
    }
    if (!adresse.trim()) {
      setFormError("Veuillez renseigner une adresse d'intervention.");
      return;
    }
    try {
      const payload = {
        clientId: userId,
        prestataireId: selectedProId,
        serviceId: selectedServiceId,
        dateHeure: `${resDate}T${resTime}:00`,
        adresse: adresse.trim(),
        montant,
      };
      const created = await createReservationMutation.mutateAsync(payload);
      const resId = created?.identifiant ?? created?.id;
      if (resId) setSelectedReservationId(resId);
      setSelectedReservationMontant(montant);
      navigateTo("paiement");
    } catch (err) {
      setFormError(err?.response?.data?.message || "Impossible de créer la réservation. Réessayez.");
    }
  }

  return (
    <React.Fragment>
  <div className="k358">
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
        <span className="k363">
          2
        </span>
        <span className="k364">
          Date & heure
        </span>
      </div>
      <span className="k365"></span>
      <div className="k199">
        <span className="k366">
          3
        </span>
        <span className="k367">
          Paiement
        </span>
      </div>
      <span className="k365"></span>
      <div className="k199">
        <span className="k366">
          4
        </span>
        <span className="k367">
          Confirmation
        </span>
      </div>
    </div>
    <div className="k368">
      <div className="k369">
        <div className="k370">
          <div className="k256">
            <h2 className="k371">
              Choisissez une date
            </h2>
            <div className="k372">
              <button className="k373">
                ‹
              </button>
              <span className="k374">
                Janvier 2026
              </span>
              <button className="k375">
                ›
              </button>
            </div>
          </div>
          <div className="k376">
            <span className="k377">
              L
            </span>
            <span className="k377">
              M
            </span>
            <span className="k377">
              M
            </span>
            <span className="k377">
              J
            </span>
            <span className="k377">
              V
            </span>
            <span className="k377">
              S
            </span>
            <span className="k377">
              D
            </span>
          </div>
          <div className="k378">
            {calDays.map((d, __i) => (
<div key={d.id ?? __i} style={d.style}>
  {d.label}
</div>
))}
          </div>
        </div>
        <div className="k370">
          <h2 className="k379">
            Créneaux disponibles
          </h2>
          <div className="k380">
            <button type="button" className={resTime === "08:00" ? "k382" : "k381"} onClick={() => setResTime("08:00")}>
              08h – 10h
            </button>
            <button type="button" className={resTime === "10:00" ? "k382" : "k381"} onClick={() => setResTime("10:00")}>
              10h – 12h
            </button>
            <button type="button" className={resTime === "14:00" ? "k382" : "k381"} onClick={() => setResTime("14:00")}>
              14h – 16h
            </button>
            <button type="button" className={resTime === "16:00" ? "k382" : "k381"} onClick={() => setResTime("16:00")}>
              16h – 18h
            </button>
            <button type="button" className={resTime === "18:00" ? "k382" : "k381"} onClick={() => setResTime("18:00")}>
              18h – 20h
            </button>
            <button type="button" className={resTime === "20:00" ? "k382" : "k381"} onClick={() => setResTime("20:00")}>
              20h – 22h
            </button>
          </div>
          <input type="date" className="k385" style={{marginTop: 12}} value={resDate} onChange={(e) => setResDate(e.target.value)} />
        </div>
        <div className="k370">
          <h2 className="k384">
            Adresse d'intervention
          </h2>
          <input className="k385" placeholder="Rue, num\u00e9ro, quartier" value={adresse} onChange={(e) => setAdresse(e.target.value)} />
          <textarea className="k386" placeholder="Pr\u00e9cisions pour le professionnel (optionnel)" value={notes} onChange={(e) => setNotes(e.target.value)}></textarea>
        </div>
      </div>
      <aside className="k267">
        <h3 className="k387">
          Récapitulatif
        </h3>
        <div className="k388">
          <span className="k389">
            {proInfo?.initials || "PR"}
          </span>
          <div>
            <div className="k32">
              {proInfo?.name || "Professionnel"}
            </div>
            <div className="k95">
              {selectedService?.title || "Service"}
            </div>
          </div>
        </div>
        <div className="k390">
          <div className="k391">
            <i className="icon fa-solid fa-calendar-days" style={{fontSize: "16px", color: "#139356"}}></i>
            {resDate || "Date à choisir"}
          </div>
          <div className="k391">
            <i className="icon fa-solid fa-clock" style={{fontSize: "16px", color: "#139356"}}></i>
            {resTime ? `${resTime} – ${resTime}` : "Créneau à choisir"}
          </div>
        </div>
        <div className="k392">
          <div className="k393">
            <span>
              Service
            </span>
            <span className="k394">
              {montant ? `${montant} Gdes` : "Sur devis"}
            </span>
          </div>
        </div>
        <div className="k395">
          <span className="k26">
            Total
          </span>
          <span className="k396">
            {montant ? `${montant} Gdes` : "Sur devis"}
          </span>
        </div>
        {formError ? (
<p style={{color: "#B91C1C", fontSize: 13.5, fontWeight: 600}}>{formError}</p>
) : null}
        <button className="k275" onClick={handleContinuer} disabled={createReservationMutation.isPending}>
          {createReservationMutation.isPending ? "Création..." : "Continuer vers le paiement"}
        </button>
      </aside>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Reserver;
