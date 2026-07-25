import React from "react";
import { useMutation } from "@tanstack/react-query";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { payerReservation } from "../api/reservations.js";
import { navigateTo } from "../router.jsx";

function Paiement() {
  const { userId } = useAuth();
  const [methode, setMethode] = React.useState("CARTE");
  const [payError, setPayError] = React.useState("");

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
    selectedReservationId,
    selectedReservationMontant,
  } = useApp();

  const payMutation = useMutation({
    mutationFn: () => payerReservation(selectedReservationId, methode, userId),
  });

  async function handlePayer() {
    setPayError("");
    if (!selectedReservationId) {
      setPayError("Aucune réservation en cours. Recommencez la réservation.");
      return;
    }
    if (!userId) {
      setPayError("Vous devez être connecté pour payer.");
      return;
    }
    try {
      await payMutation.mutateAsync();
      navigateTo("confirm");
    } catch (err) {
      setPayError(err?.response?.data?.message || "Le paiement a échoué. Réessayez.");
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
        <span className="k360">
          <i className="icon fa-solid fa-check" style={{fontSize: "15px", color: "#fff"}}></i>
        </span>
        <span className="k361">
          Date & heure
        </span>
      </div>
      <span className="k362"></span>
      <div className="k199">
        <span className="k363">
          3
        </span>
        <span className="k364">
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
      <div className="k88">
        <h2 className="k397">
          Mode de paiement
        </h2>
        <p className="k398">
          Vos informations sont chiffrées et sécurisées.
        </p>
        <div className="k399">
          <label className={methode === "CARTE" ? "k400" : "k403"} onClick={() => setMethode("CARTE")}>
            <span className="k401"></span>
            <span className="k402">
              CB
            </span>
            <div className="k22">
              <div className="k23">
                Carte bancaire
              </div>
              <div className="k95">
                Visa, Mastercard
              </div>
            </div>
          </label>
          <label className={methode === "MONCASH" ? "k400" : "k403"} onClick={() => setMethode("MONCASH")}>
            <span className="k404"></span>
            <span className="k405">
              MON
            </span>
            <div className="k22">
              <div className="k23">
                MonCash
              </div>
              <div className="k95">
                Paiement mobile Digicel
              </div>
            </div>
          </label>
        </div>
        <div className="k406">
          <div>
            <label className="k307">
              Numéro de carte
            </label>
            <div className="k407">
              <i className="icon fa-solid fa-credit-card" style={{fontSize: "20px", color: "#9CA3AF"}}></i>
              <input className="k408" defaultValue="4242 4242 4242 4242" />
            </div>
          </div>
          <div className="k332">
            <div>
              <label className="k307">
                Expiration
              </label>
              <input className="k409" defaultValue="12 / 27" />
            </div>
            <div>
              <label className="k307">
                CVC
              </label>
              <input className="k409" defaultValue="\u2022\u2022\u2022" />
            </div>
          </div>
          <div>
            <label className="k307">
              Nom sur la carte
            </label>
            <input className="k409" defaultValue="Peter Joseph" />
          </div>
        </div>
      </div>
      <aside className="k267">
        <h3 className="k387">
          Récapitulatif paiement
        </h3>
        <div className="k410">
          <div className="k393">
            <span>
              Montant
            </span>
            <span className="k394">
              {selectedReservationMontant ? `${selectedReservationMontant} Gdes` : "Sur devis"}
            </span>
          </div>
        </div>
        <div className="k395">
          <span className="k26">
            Total
          </span>
          <span className="k396">
            {selectedReservationMontant ? `${selectedReservationMontant} Gdes` : "Sur devis"}
          </span>
        </div>
        {payError ? (
<p style={{color: "#B91C1C", fontSize: 13.5, fontWeight: 600}}>{payError}</p>
) : null}
        <button className="k275" onClick={handlePayer} disabled={payMutation.isPending}>
          {payMutation.isPending ? "Paiement..." : `Payer ${selectedReservationMontant || ""} Gdes`}
        </button>
        <div className="k276">
          <i className="icon fa-solid fa-lock" style={{fontSize: "15px", color: "#9CA3AF"}}></i>
          Paiement sécurisé · SSL
        </div>
      </aside>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Paiement;
