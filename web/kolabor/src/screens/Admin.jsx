import React from "react";
import { useApp } from "../AppContext.jsx";

function Admin() {
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
  <div className="k664">
    <aside className="k665">
      <div className="k666">
        <span className="k667">
          K
        </span>
        <div>
          <div className="k668">
            Kolabor
          </div>
          <div className="k669">
            ADMIN
          </div>
        </div>
      </div>
      <div className="k429">
        <div className="k670">
          <i className="icon fa-solid fa-building" style={{fontSize: "18px", color: "#0F274A"}}></i>
          Tableau de bord
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-user" style={{fontSize: "18px", color: "currentColor"}}></i>
          Utilisateurs
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-shield" style={{fontSize: "18px", color: "currentColor"}}></i>
          Professionnels
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-table-columns" style={{fontSize: "18px", color: "currentColor"}}></i>
          Services
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-calendar-days" style={{fontSize: "18px", color: "currentColor"}}></i>
          Réservations
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-credit-card" style={{fontSize: "18px", color: "currentColor"}}></i>
          Paiements
        </div>
        <div className="k671">
          <i className="icon fa-solid fa-chart-line" style={{fontSize: "18px", color: "currentColor"}}></i>
          Statistiques
        </div>
      </div>
    </aside>
    <div>
      <div className="k190">
        <div>
          <h1 className="k493">
            Tableau de bord
          </h1>
          <p className="k494">
            Vue d'ensemble de la plateforme Kolabor.
          </p>
        </div>
        <div className="k672">
          <span className="k673">
            AD
          </span>
          <span className="k364">
            Admin
          </span>
        </div>
      </div>
      <div className="k433">
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Utilisateurs
            </span>
            <span className="k675">
              <i className="icon fa-solid fa-user" style={{fontSize: "17px", color: "#2563EB"}}></i>
            </span>
          </div>
          <div className="k676">
            48 250
          </div>
          <div className="k498">
            ▲ 8,2% ce mois
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Professionnels
            </span>
            <span className="k677">
              <i className="icon fa-solid fa-shield" style={{fontSize: "17px", color: "#139356"}}></i>
            </span>
          </div>
          <div className="k676">
            12 480
          </div>
          <div className="k498">
            ▲ 5,1% ce mois
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Réservations
            </span>
            <span className="k678">
              <i className="icon fa-solid fa-calendar-days" style={{fontSize: "17px", color: "#F59E0B"}}></i>
            </span>
          </div>
          <div className="k676">
            3 842
          </div>
          <div className="k498">
            ▲ 12,4% ce mois
          </div>
        </div>
        <div className="k674">
          <div className="k190">
            <span className="k15">
              Revenus (mois)
            </span>
            <span className="k677">
              <i className="icon fa-solid fa-money-bill-wave" style={{fontSize: "17px", color: "#139356"}}></i>
            </span>
          </div>
          <div className="k679">
            2,4M
            <span className="k437">
              Gdes
            </span>
          </div>
          <div className="k498">
            ▲ 9,8% ce mois
          </div>
        </div>
      </div>
      <div className="k680">
        <div className="k681">
          <div className="k256">
            <h2 className="k505">
              Revenus (6 mois)
            </h2>
            <span className="k682">
              +18,3%
            </span>
          </div>
          <div className="k683">
            <div className="k516">
              <div className="k684"></div>
              <span className="k518">
                Aoû
              </span>
            </div>
            <div className="k516">
              <div className="k685"></div>
              <span className="k518">
                Sep
              </span>
            </div>
            <div className="k516">
              <div className="k686"></div>
              <span className="k518">
                Oct
              </span>
            </div>
            <div className="k516">
              <div className="k687"></div>
              <span className="k518">
                Nov
              </span>
            </div>
            <div className="k516">
              <div className="k688"></div>
              <span className="k518">
                Déc
              </span>
            </div>
            <div className="k516">
              <div className="k689"></div>
              <span className="k518">
                Jan
              </span>
            </div>
          </div>
        </div>
        <div className="k681">
          <h2 className="k514">
            Top catégories
          </h2>
          <div className="k406">
            <div>
              <div className="k690">
                <span className="k691">
                  Ménage
                </span>
                <span className="k692">
                  32%
                </span>
              </div>
              <div className="k693">
                <span className="k694"></span>
              </div>
            </div>
            <div>
              <div className="k690">
                <span className="k691">
                  Plomberie
                </span>
                <span className="k692">
                  26%
                </span>
              </div>
              <div className="k693">
                <span className="k695"></span>
              </div>
            </div>
            <div>
              <div className="k690">
                <span className="k691">
                  Électricité
                </span>
                <span className="k692">
                  21%
                </span>
              </div>
              <div className="k693">
                <span className="k696"></span>
              </div>
            </div>
            <div>
              <div className="k690">
                <span className="k691">
                  Climatisation
                </span>
                <span className="k692">
                  12%
                </span>
              </div>
              <div className="k693">
                <span className="k697"></span>
              </div>
            </div>
            <div>
              <div className="k690">
                <span className="k691">
                  Autres
                </span>
                <span className="k692">
                  9%
                </span>
              </div>
              <div className="k693">
                <span className="k698"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="k699">
        <div className="k700">
          <h2 className="k505">
            Gestion des utilisateurs
          </h2>
          <div className="k199">
            <div className="k701">
              <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "16px", color: "#9CA3AF"}}></i>
              <input className="k702" placeholder="Rechercher\u2026" />
            </div>
            <button className="k213">
              + Ajouter
            </button>
          </div>
        </div>
        <div className="k703">
          <span>
            Utilisateur
          </span>
          <span>
            Rôle
          </span>
          <span>
            Ville
          </span>
          <span>
            Statut
          </span>
          <span className="k28">
            Actions
          </span>
        </div>
        {adminUsers.map((u, __i) => (
<div key={u.id ?? __i} className="k704">
  <div className="k20">
    <span className="k705" style={{background: u.color}}>
      {u.initials}
    </span>
    <div className="k706">
      <div className="k707">
        {u.name}
      </div>
      <div className="k708">
        {u.email}
      </div>
    </div>
  </div>
  <span style={u.roleStyle}>
    {u.role}
  </span>
  <span className="k709">
    {u.city}
  </span>
  <span>
    <span style={u.statusStyle}>
      {u.status}
    </span>
  </span>
  <div className="k710">
    <button className="k711">
      <i className="icon fa-solid fa-pen" style={{fontSize: "15px", color: "currentColor"}}></i>
    </button>
    <button className="k712">
      <i className="icon fa-solid fa-trash" style={{fontSize: "15px", color: "currentColor"}}></i>
    </button>
  </div>
</div>
))}
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Admin;
