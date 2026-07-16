import React from "react";
import { useApp } from "../AppContext.jsx";

function DashPro() {
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
  <div className="k424">
    <aside className="k425">
      <div className="k426">
        <span className="k427">
          MF
        </span>
        <div>
          <div className="k23">
            Marc Fontaine
          </div>
          <div className="k489">
            Pro · Vérifié
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k431">
          <i className="icon fa-solid fa-building" style={{fontSize: "18px", color: "#fff"}}></i>
          Tableau de bord
        </div>
        <div className="k430" onClick={nav.messervices}>
          <i className="icon fa-solid fa-table-columns" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Mes services
        </div>
        <div className="k430" onClick={nav.dispos}>
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Disponibilités
        </div>
        <div className="k490">
          <span className="k491">
            <i className="icon fa-solid fa-heart-pulse" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
            Demandes reçues
          </span>
          <span className="k492">
            3
          </span>
        </div>
        <div className="k430" onClick={nav.revenus}>
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Revenus
        </div>
        <div className="k430">
          <i className="icon fa-solid fa-star" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Avis clients
        </div>
        <div className="k430">
          <i className="icon fa-solid fa-chart-line" style={{fontSize: "18px", color: "#9CA3AF"}}></i>
          Statistiques
        </div>
      </div>
    </aside>
    <div>
      <div className="k190">
        <div>
          <h1 className="k493">
            Bonjour, Marc 👋
          </h1>
          <p className="k494">
            Voici votre activité cette semaine.
          </p>
        </div>
        <button className="k495">
          <i className="icon fa-solid fa-plus" style={{fontSize: "17px", color: "#fff"}}></i>
          Ajouter un service
        </button>
      </div>
      <div className="k433">
        <div className="k434">
          <div className="k15">
            Revenus du mois
          </div>
          <div className="k496">
            18 400
            <span className="k497">
              Gdes
            </span>
          </div>
          <div className="k498">
            ▲ 12% vs mois dernier
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Demandes en attente
          </div>
          <div className="k499">
            3
          </div>
          <div className="k500">
            À traiter aujourd'hui
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Note moyenne
          </div>
          <div className="k499">
            4,8
            <span className="k501">
              ★
            </span>
          </div>
          <div className="k502">
            127 avis
          </div>
        </div>
        <div className="k434">
          <div className="k15">
            Réservations
          </div>
          <div className="k499">
            42
          </div>
          <div className="k502">
            ce mois-ci
          </div>
        </div>
      </div>
      <div className="k503">
        <div className="k443">
          <div className="k504">
            <h2 className="k505">
              Demandes reçues
            </h2>
            <span className="k506">
              Tout voir
            </span>
          </div>
          <div className="k248">
            <div className="k507">
              <span className="k508">
                SM
              </span>
              <div className="k22">
                <div className="k471">
                  Sophie Martin
                </div>
                <div className="k24">
                  Fuite d'eau · Delmas · 16 Jan
                </div>
              </div>
              <div className="k509">
                <button className="k510">
                  <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
                </button>
                <button className="k511">
                  <i className="icon fa-solid fa-xmark" style={{fontSize: "15px", color: "currentColor"}}></i>
                </button>
              </div>
            </div>
            <div className="k507">
              <span className="k512">
                RJ
              </span>
              <div className="k22">
                <div className="k471">
                  Ricardo Joseph
                </div>
                <div className="k24">
                  Débouchage · Pétion-Ville · 17 Jan
                </div>
              </div>
              <div className="k509">
                <button className="k510">
                  <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
                </button>
                <button className="k511">
                  <i className="icon fa-solid fa-xmark" style={{fontSize: "15px", color: "currentColor"}}></i>
                </button>
              </div>
            </div>
            <div className="k507">
              <span className="k513">
                GC
              </span>
              <div className="k22">
                <div className="k471">
                  Gladys Charles
                </div>
                <div className="k24">
                  Installation · Carrefour · 19 Jan
                </div>
              </div>
              <div className="k509">
                <button className="k510">
                  <i className="icon fa-solid fa-check" style={{fontSize: "17px", color: "#fff"}}></i>
                </button>
                <button className="k511">
                  <i className="icon fa-solid fa-xmark" style={{fontSize: "15px", color: "currentColor"}}></i>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="k443">
          <h2 className="k514">
            Revenus (7 jours)
          </h2>
          <div className="k515">
            <div className="k516">
              <div className="k517"></div>
              <span className="k518">
                L
              </span>
            </div>
            <div className="k516">
              <div className="k519"></div>
              <span className="k518">
                M
              </span>
            </div>
            <div className="k516">
              <div className="k520"></div>
              <span className="k518">
                M
              </span>
            </div>
            <div className="k516">
              <div className="k521"></div>
              <span className="k518">
                J
              </span>
            </div>
            <div className="k516">
              <div className="k522"></div>
              <span className="k518">
                V
              </span>
            </div>
            <div className="k516">
              <div className="k523"></div>
              <span className="k518">
                S
              </span>
            </div>
            <div className="k516">
              <div className="k524"></div>
              <span className="k518">
                D
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default DashPro;
