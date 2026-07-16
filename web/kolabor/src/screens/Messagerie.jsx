import React from "react";
import { useApp } from "../AppContext.jsx";

function Messagerie() {
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
  <div className="k461">
    <div className="k462">
      <div className="k463">
        <div className="k464">
          <h2 className="k257">
            Messages
          </h2>
          <div className="k465">
            <i className="icon fa-solid fa-magnifying-glass" style={{fontSize: "17px", color: "#9CA3AF"}}></i>
            <input className="k466" placeholder="Rechercher\u2026" />
          </div>
        </div>
        <div className="k467">
          {convList.map((c, __i) => (
<div key={c.id ?? __i} onClick={c.pick} style={c.rowStyle}>
  <span className="k468" style={{background: c.color}}>
    {c.initials}
    {c.online ? (
<React.Fragment>
      <span className="k469"></span>
</React.Fragment>
) : null}
  </span>
  <div className="k470">
    <div className="k393">
      <span className="k471">
        {c.name}
      </span>
      <span className="k27">
        {c.time}
      </span>
    </div>
    <div className="k258">
      <div className="k472">
        {c.last}
      </div>
      {c.unread ? (
<React.Fragment>
        <span className="k473">
          {c.unread}
        </span>
</React.Fragment>
) : null}
    </div>
  </div>
</div>
))}
        </div>
      </div>
      <div className="k474">
        <div className="k475">
          <span className="k476" style={{background: activeConv.color}}>
            {activeConv.initials}
          </span>
          <div className="k22">
            <div className="k477">
              <span className="k478">
                {activeConv.name}
              </span>
              <i className="icon fa-solid fa-shield-halved" style={{fontSize: "14px", color: "#22C55E"}}></i>
            </div>
            <div className="k479" style={{color: activeConv.statusColor}}>
              {activeConv.statusText}
            </div>
          </div>
          <button className="k480" onClick={nav.profil}>
            Voir le profil
          </button>
        </div>
        <div className="k481">
          <div className="k35">
            <span className="k482">
              Aujourd'hui
            </span>
          </div>
          {activeConv.msgs.map((m, __i) => (
<div key={m.id ?? __i} style={m.rowStyle}>
  {m.image ? (
<React.Fragment>
    <div>
      <div className="k483"></div>
      <span className="k484">
        {m.time}
      </span>
    </div>
</React.Fragment>
) : null}
  {m.text ? (
<React.Fragment>
    <div style={m.bubbleStyle}>
      {m.text}
      <span style={m.timeStyle}>
        {m.time}
      </span>
    </div>
</React.Fragment>
) : null}
</div>
))}
        </div>
        <div className="k485">
          <button className="k486">
            <i className="icon fa-solid fa-image" style={{fontSize: "20px", color: "currentColor"}}></i>
          </button>
          <input className="k487" value={draft} onChange={onDraft} onKeyDown={onKey} placeholder="\u00c9crivez votre message\u2026" />
          <button className="k488" onClick={sendMsg}>
            Envoyer
            <i className="icon fa-solid fa-paper-plane" style={{fontSize: "16px", color: "#fff"}}></i>
          </button>
        </div>
      </div>
    </div>
  </div>
    </React.Fragment>
  );
}

export default Messagerie;
