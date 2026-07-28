import React from "react";
import { useApp } from "../AppContext.jsx";
import { useAuth } from "../AuthContext.jsx";
import { navigateTo } from "../router.jsx";

function formatDateHeure(dateHeure) {
  if (!dateHeure) return "";
  const [datePart, timePart] = String(dateHeure).split("T");
  const time = (timePart || "").slice(0, 5);
  return time ? `${datePart} · ${time}` : datePart;
}

function Panier() {
  const { isAuthenticated } = useAuth();
  const {
    panier,
    panierCount,
    panierTotal,
    retirerDuPanier,
    validerPanier,
    panierCheckoutPending,
    panierCheckoutError,
    setPanierCheckoutError,
  } = useApp();

  React.useEffect(() => {
    // Nettoie un message d'erreur d'une précédente tentative quand on
    // (re)visite la page avec un panier vide.
    if (panier.length === 0 && panierCheckoutError) setPanierCheckoutError("");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="k358">
      <h1 className="k229" style={{marginBottom: 24}}>
        Mon panier
      </h1>
      {panier.length === 0 ? (
        <div className="k370" style={{textAlign: "center", padding: "48px 24px"}}>
          <p style={{color: "#6B7280", fontSize: 15, marginBottom: 18}}>
            Votre panier est vide. Ajoutez un ou plusieurs services depuis la page de réservation pour les payer en une seule fois.
          </p>
          <button className="k253" onClick={() => navigateTo("services")}>
            Parcourir les services
          </button>
        </div>
      ) : (
        <div className="k368">
          <div className="k369">
            <div className="k370">
              <h2 className="k371" style={{marginBottom: 14}}>
                Services sélectionnés ({panierCount})
              </h2>
              <div className="k248">
                {panier.map((item) => (
                  <div className="k249" key={item.cartItemId}>
                    <div>
                      <div className="k23">
                        {item.serviceTitle}
                      </div>
                      <div className="k250">
                        {item.proName} · {formatDateHeure(item.dateHeure)} · {item.adresse}
                      </div>
                    </div>
                    <div className="k251">
                      <span className="k252">
                        {item.montant ? `${item.montant} Gdes` : "Sur devis"}
                      </span>
                      <button type="button" className="k253" onClick={() => retirerDuPanier(item.cartItemId)}>
                        Retirer
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
          <aside className="k267">
            <h3 className="k387">
              Récapitulatif
            </h3>
            <div className="k395">
              <span className="k26">
                Total
              </span>
              <span className="k396">
                {panierTotal ? `${panierTotal} Gdes` : "Sur devis"}
              </span>
            </div>
            {!isAuthenticated ? (
<p style={{color: "#9CA3AF", fontSize: 13, marginTop: 12}}>Connectez-vous pour valider votre panier.</p>
) : null}
            {panierCheckoutError ? (
<p style={{color: "#B91C1C", fontSize: 13.5, fontWeight: 600, marginTop: 12}}>{panierCheckoutError}</p>
) : null}
            <button
              type="button"
              className="k275"
              style={{marginTop: 16}}
              onClick={validerPanier}
              disabled={panierCheckoutPending || !isAuthenticated}
            >
              {panierCheckoutPending ? "Réservation en cours..." : "Réserver et payer"}
            </button>
            <button
              type="button"
              onClick={() => navigateTo("reserver")}
              style={{width: "100%", marginTop: 10, background: "none", border: "none", color: "#19355F", fontWeight: 700, fontSize: 13.5, cursor: "pointer"}}
            >
              Ajouter un autre service
            </button>
          </aside>
        </div>
      )}
    </div>
  );
}

export default Panier;
