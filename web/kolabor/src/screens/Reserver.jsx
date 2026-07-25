import React from "react";
import { useMutation } from "@tanstack/react-query";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { createReservation } from "../api/reservations.js";
import { navigateTo } from "../router.jsx";

const MONTH_NAMES = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];

const TIME_SLOTS = [
  { value: "08:00", label: "08h – 10h" },
  { value: "10:00", label: "10h – 12h" },
  { value: "14:00", label: "14h – 16h" },
  { value: "16:00", label: "16h – 18h" },
  { value: "18:00", label: "18h – 20h" },
  { value: "20:00", label: "20h – 22h" },
];

function todayStr() {
  const t = new Date();
  return `${t.getFullYear()}-${String(t.getMonth() + 1).padStart(2, "0")}-${String(t.getDate()).padStart(2, "0")}`;
}

// Le backend rejette toute réservation dont la date/heure n'est pas
// strictement dans le futur (@Future sur ReservationRequest.dateHeure).
// On bloque donc côté client les créneaux déjà passés si la date choisie
// est aujourd'hui, pour ne pas laisser l'utilisateur soumettre une
// combinaison vouée à échouer avec une erreur 400 peu explicite.
function isSlotPast(dateStr, timeValue, min) {
  if (dateStr !== min) return false;
  const [h, m] = timeValue.split(":").map(Number);
  const slot = new Date();
  slot.setHours(h, m, 0, 0);
  return slot.getTime() <= Date.now();
}

// Extrait un message d'erreur exploitable de la réponse du backend.
// GlobalExceptionHandler renvoie soit {message: "..."} soit, pour les
// erreurs de validation (@Valid), une map {champ: "message"} sans clé
// "message" — ce qui faisait retomber l'UI sur un texte générique qui
// cachait la vraie raison (ex. "La date doit être dans le futur").
function extractApiErrorMessage(err) {
  const data = err?.response?.data;
  if (!data) return null;
  if (typeof data === "string") return data;
  if (typeof data === "object") {
    if (typeof data.message === "string") return data.message;
    const fieldMessages = Object.values(data).filter((v) => typeof v === "string");
    if (fieldMessages.length) return fieldMessages.join(" ");
  }
  return null;
}

function Reserver() {
  const { userId } = useAuth();
  const now = React.useMemo(() => new Date(), []);
  const [viewYear, setViewYear] = React.useState(now.getFullYear());
  const [viewMonth, setViewMonth] = React.useState(now.getMonth());
  const [resDate, setResDate] = React.useState("");
  const [resTime, setResTime] = React.useState("");
  const [adresse, setAdresse] = React.useState("12, Rue Pinchinat, Pétion-Ville");
  const [notes, setNotes] = React.useState("");
  const [formError, setFormError] = React.useState("");

  const min = todayStr();

  const calendarCells = React.useMemo(() => {
    const firstDay = new Date(viewYear, viewMonth, 1);
    const leadingBlanks = (firstDay.getDay() + 6) % 7;
    const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();
    const cells = [];
    for (let i = 0; i < leadingBlanks; i++) cells.push({ blank: true, key: `b${i}` });
    for (let d = 1; d <= daysInMonth; d++) {
      const dateStr = `${viewYear}-${String(viewMonth + 1).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
      cells.push({
        key: dateStr,
        day: d,
        dateStr,
        isPast: dateStr < min,
        isSelected: dateStr === resDate,
      });
    }
    return cells;
  }, [viewYear, viewMonth, resDate, min]);

  function goPrevMonth() {
    if (viewMonth === 0) { setViewMonth(11); setViewYear((y) => y - 1); }
    else setViewMonth((m) => m - 1);
  }

  function goNextMonth() {
    if (viewMonth === 11) { setViewMonth(0); setViewYear((y) => y + 1); }
    else setViewMonth((m) => m + 1);
  }

  function pickDay(dateStr, isPast) {
    if (isPast) return;
    setResDate(dateStr);
  }

  // Si l'utilisateur change de date et que le créneau déjà sélectionné se
  // retrouve dans le passé pour la nouvelle date (typiquement en revenant à
  // "aujourd'hui"), on le désélectionne pour éviter une soumission vouée à
  // être rejetée par le backend.
  React.useEffect(() => {
    if (resTime && isSlotPast(resDate, resTime, min)) {
      setResTime("");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [resDate]);

  const {
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
    if (resDate < min) {
      setFormError("La date choisie est déjà passée. Veuillez choisir une date à venir.");
      return;
    }
    if (isSlotPast(resDate, resTime, min)) {
      setFormError("Ce créneau est déjà passé. Veuillez choisir un autre créneau.");
      return;
    }
    if (!adresse.trim()) {
      setFormError("Veuillez renseigner une adresse d'intervention.");
      return;
    }
    if (!userId) {
      setFormError("Vous devez être connecté pour réserver.");
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
      setFormError(extractApiErrorMessage(err) || "Impossible de créer la réservation. Réessayez.");
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
              <button type="button" className="k373" onClick={goPrevMonth}>
                ‹
              </button>
              <span className="k374">
                {MONTH_NAMES[viewMonth]} {viewYear}
              </span>
              <button type="button" className="k375" onClick={goNextMonth}>
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
            {calendarCells.map((d) => d.blank ? (
<div key={d.key}></div>
) : (
<div
  key={d.key}
  onClick={() => pickDay(d.dateStr, d.isPast)}
  style={{
    aspectRatio: "1",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 10,
    fontSize: 14,
    fontWeight: 600,
    cursor: d.isPast ? "not-allowed" : "pointer",
    color: d.isPast ? "#D1D5DB" : d.isSelected ? "#fff" : "#374151",
    background: d.isSelected ? "#139356" : "transparent",
    border: d.isSelected ? "1.5px solid #139356" : "1.5px solid transparent",
  }}
>
  {d.day}
</div>
))}
          </div>
        </div>
        <div className="k370">
          <h2 className="k379">
            Créneaux disponibles
          </h2>
          <div className="k380">
            {TIME_SLOTS.map((slot) => {
              const disabled = isSlotPast(resDate, slot.value, min);
              return (
                <button
                  key={slot.value}
                  type="button"
                  className={resTime === slot.value ? "k382" : "k381"}
                  disabled={disabled}
                  title={disabled ? "Ce créneau est déjà passé aujourd'hui" : undefined}
                  style={disabled ? { opacity: 0.4, cursor: "not-allowed" } : undefined}
                  onClick={() => setResTime(slot.value)}
                >
                  {slot.label}
                </button>
              );
            })}
          </div>
          <input type="date" className="k385" style={{marginTop: 12}} min={min} value={resDate} onChange={(e) => setResDate(e.target.value)} />
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
            {resTime ? `${resTime} – ${String((parseInt(resTime, 10) + 2) % 24).padStart(2, "0")}:00` : "Créneau à choisir"}
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
