import React from "react";
import { useHashPath, screenForPath, navigateTo } from "./router.jsx";

// Etat global de l'application + toutes les données de démonstration.
// Porté depuis le prototype d'origine (state + renderVals()) vers des hooks React classiques.

const AppContext = React.createContext(null);

function useApp() {
  const ctx = React.useContext(AppContext);
  if (!ctx) throw new Error("useApp doit être utilisé sous <AppProvider>");
  return ctx;
}

function normalize(x) {
  return (x || "").toString().toLowerCase().normalize("NFD").replace(/[̀-ͯ]/g, "");
}

// ---- données statiques (issues du prototype) ----

const PROS_BASE = [
  { id: 1, cat: "Plomberie", ratingNum: 4.8, priceNum: 250, initials: "MF", name: "Marc Fontaine", job: "Plombier certifié", city: "Pétion-Ville", rating: "4,8", reviews: 127, price: "250 Gdes/h", avatar: "#19355F", verified: true, available: true, popular: false },
  { id: 2, cat: "Électricité", ratingNum: 4.9, priceNum: 300, initials: "NJ", name: "Naïka Joseph", job: "Électricienne", city: "Delmas", rating: "4,9", reviews: 89, price: "300 Gdes/h", avatar: "#0F7A48", verified: true, available: true, popular: true },
  { id: 3, cat: "Jardinage", ratingNum: 4.7, priceNum: 180, initials: "JB", name: "Jean Baptiste", job: "Jardinier paysagiste", city: "Port-au-Prince", rating: "4,7", reviews: 64, price: "180 Gdes/h", avatar: "#4B5563", verified: true, available: true, popular: false },
  { id: 4, cat: "Ménage", ratingNum: 5.0, priceNum: 150, initials: "RP", name: "Roselène Pierre", job: "Aide-ménagère", city: "Carrefour", rating: "5,0", reviews: 152, price: "150 Gdes/h", avatar: "#139356", verified: true, available: false, popular: true },
];

const PROS_MORE = [
  { id: 5, cat: "Climatisation", ratingNum: 4.6, priceNum: 400, initials: "WD", name: "Wesley Dorvil", job: "Climatisation", city: "Port-au-Prince", rating: "4,6", reviews: 73, price: "400 Gdes/h", avatar: "#2563EB", verified: true, available: true, popular: false },
  { id: 6, cat: "Peinture", ratingNum: 4.8, priceNum: 220, initials: "GC", name: "Gladys Charles", job: "Peintre en bâtiment", city: "Cap-Haïtien", rating: "4,8", reviews: 41, price: "220 Gdes/h", avatar: "#7C3AED", verified: true, available: true, popular: false },
  { id: 7, cat: "Électricité", ratingNum: 4.5, priceNum: 280, initials: "FE", name: "Frantz Étienne", job: "Électricien", city: "Les Cayes", rating: "4,5", reviews: 38, price: "280 Gdes/h", avatar: "#F59E0B", verified: true, available: false, popular: false },
  { id: 8, cat: "Ménage", ratingNum: 4.9, priceNum: 160, initials: "MD", name: "Mirlande Dély", job: "Aide-ménagère", city: "Delmas", rating: "4,9", reviews: 96, price: "160 Gdes/h", avatar: "#0F7A48", verified: true, available: true, popular: false },
];

const SERVICES = [
  { id: 1, cat: "Plomberie", title: "Réparation de fuite d’eau", price: "250 Gdes", rating: "4,9", tag: "#19355F" },
  { id: 2, cat: "Électricité", title: "Installation électrique", price: "300 Gdes", rating: "4,8", tag: "#B45309" },
  { id: 3, cat: "Ménage", title: "Nettoyage complet maison", price: "150 Gdes", rating: "5,0", tag: "#7C3AED" },
  { id: 4, cat: "Climatisation", title: "Entretien climatiseur", price: "400 Gdes", rating: "4,6", tag: "#2563EB" },
  { id: 5, cat: "Jardinage", title: "Entretien de jardin", price: "180 Gdes", rating: "4,7", tag: "#139356" },
  { id: 6, cat: "Peinture", title: "Peinture intérieure", price: "220 Gdes", rating: "4,8", tag: "#4B5563" },
  { id: 7, cat: "Plomberie", title: "Débouchage canalisation", price: "200 Gdes", rating: "4,7", tag: "#0F7A48" },
  { id: 8, cat: "Électricité", title: "Dépannage urgent", price: "350 Gdes", rating: "4,9", tag: "#19355F" },
];

const CATALOGUE_BASE = [
  { name: "Entretien de la maison", color: "#7C3AED", bg: "#F3E8FF", items: ["Ménage complet", "Nettoyage de printemps", "Nettoyage après déménagement", "Nettoyage après travaux", "Lavage des vitres", "Nettoyage des sols", "Dépoussiérage", "Désinfection", "Lavage de tapis", "Nettoyage de canapé", "Nettoyage de matelas", "Nettoyage de rideaux"] },
  { name: "Lessive et Repassage", color: "#0EA5E9", bg: "#E0F2FE", items: ["Lessive", "Repassage", "Pliage du linge", "Lavage à sec (collecte/livraison)", "Organisation du dressing"] },
  { name: "Plomberie", color: "#19355F", bg: "#DBEAFE", items: ["Réparation de fuite", "Débouchage", "Installation de robinet", "Installation de lavabo", "Installation de douche", "Installation de toilettes", "Réparation de chauffe-eau", "Remplacement de la tuyauterie"] },
  { name: "Électricité", color: "#F59E0B", bg: "#FEF3C7", items: ["Installation de luminaires", "Pose de prises électriques", "Installation d’interrupteurs", "Ventilateurs de plafond", "Réparation de panne électrique", "Mise aux normes électriques", "Installation de tableau électrique"] },
  { name: "Peinture", color: "#EC4899", bg: "#FCE7F3", items: ["Peinture intérieure", "Peinture extérieure", "Peinture de plafond", "Peinture de portes", "Peinture de fenêtres", "Pose de papier peint", "Retouches de peinture"] },
  { name: "Jardinage", color: "#139356", bg: "#D8F3E4", items: ["Tonte de pelouse", "Taille de haies", "Désherbage", "Plantation", "Entretien du jardin", "Arrosage", "Élagage"] },
  { name: "Menuiserie", color: "#92400E", bg: "#FEF3C7", items: ["Montage de meubles", "Meubles sur mesure", "Réparation de meubles", "Pose d’étagères", "Installation de portes", "Installation de fenêtres", "Pose de pancartes"] },
  { name: "Maçonnerie", color: "#6B7280", bg: "#F3F4F6", items: ["Construction de murs", "Réparation de murs", "Pose de carrelage", "Réparation de carrelage", "Création de terrasse", "Création d’allées", "Bétonnage"] },
  { name: "Climatisation & Ventilation", color: "#2563EB", bg: "#DBEAFE", items: ["Installation de climatisation", "Entretien de climatiseur", "Nettoyage des filtres", "Réparation de climatiseur", "Installation de ventilation"] },
  { name: "Déménagement", color: "#0F7A48", bg: "#D8F3E4", items: ["Déménagement local", "Déménagement longue distance", "Emballage", "Déballage", "Manutention", "Déchargement"] },
  { name: "Livraison", color: "#DC2626", bg: "#FEE2E2", items: ["Livraison de meubles", "Livraison d’électroménager", "Livraison de colis", "Livraison express"] },
  { name: "Informatique & Technologie", color: "#4F46E5", bg: "#E0E7FF", items: ["Dépannage informatique", "Installation Wi-Fi", "Installation réseau", "Assistance informatique", "Caméra de surveillance", "Maintenance"] },
  { name: "Déchets et débarras", color: "#65A30D", bg: "#ECFCCB", items: ["Enlèvement de déchets", "Débarras de maison", "Débarras de garage", "Débarras de cave", "Recyclage", "Enlèvement d’encombrants"] },
  { name: "Désinfection & Antiparasitaire", color: "#B91C1C", bg: "#FEE2E2", items: ["Désinsectisation", "Dératisation", "Traitement anti-termites", "Désinfection complète", "Fumigation"] },
  { name: "Installation d’appareils", color: "#0891B2", bg: "#CFFAFE", items: ["Installation murale TV", "Installation lave-linge", "Installation lave-vaisselle", "Installation four", "Installation réfrigérateur", "Installation hotte", "Installation chauffe-eau"] },
  { name: "Sécurité", color: "#1E293B", bg: "#E2E8F0", items: ["Installation d’alarme", "Changement de serrure", "Installation de portail", "Installation de caméra", "Contrôle d’accès"] },
  { name: "Aménagement paysager", color: "#16A34A", bg: "#DCFCE7", items: ["Création de jardin", "Pose de gazon", "Arrosage automatique", "Élagage", "Décoration extérieure"] },
  { name: "Piscine", color: "#06B6D4", bg: "#CFFAFE", items: ["Nettoyage de piscine", "Traitement de l’eau", "Entretien régulier", "Réparation des équipements"] },
  { name: "Services automobiles", color: "#475569", bg: "#F1F5F9", items: ["Lavage de voiture", "Nettoyage intérieur", "Polissage", "Changement de batterie", "Remplacement de pneus", "Assistance batterie"] },
  { name: "Famille et assistance", color: "#DB2777", bg: "#FCE7F3", items: ["Garde d’enfants", "Aide aux devoirs", "Accompagnement scolaire", "Garde de personnes âgées", "Assistance à domicile"] },
  { name: "Animaux", color: "#CA8A04", bg: "#FEF9C3", items: ["Promenade de chiens", "Garde d’animaux", "Toilettage", "Visite à domicile", "Nettoyage des espaces"] },
  { name: "Amélioration de l’habitat", color: "#7C2D12", bg: "#FFEDD5", items: ["Rénovation intérieure", "Rénovation extérieure", "Isolation", "Faux plafond", "Pose de parquet", "Installation de cuisine", "Installation de salle de bain"] },
  { name: "Fenêtres & Portes", color: "#0D9488", bg: "#CCFBF1", items: ["Réparation de fenêtres", "Installation de fenêtres", "Réparation de portes", "Installation de portes", "Pose de moustiquaires", "Installation de rideaux"] },
  { name: "Énergie solaire", color: "#EA580C", bg: "#FFEDD5", items: ["Installation de panneaux solaires", "Entretien des panneaux", "Installation de batteries", "Audit énergétique"] },
];

const CAT_PRICE = ["150 Gdes","120 Gdes","250 Gdes","300 Gdes","220 Gdes","180 Gdes","260 Gdes","280 Gdes","400 Gdes","500 Gdes","200 Gdes","350 Gdes","160 Gdes","320 Gdes","240 Gdes","380 Gdes","300 Gdes","350 Gdes","270 Gdes","200 Gdes","150 Gdes","450 Gdes","240 Gdes","600 Gdes"];
const CAT_COUNT = [412,87,340,285,198,156,93,71,124,64,52,112,58,41,79,63,88,37,74,96,55,68,49,33];
const CAT_RATE = ["4,9","4,8","4,8","4,9","4,7","4,8","4,6","4,7","4,6","4,8","4,7","4,9","4,6","4,8","4,7","4,9","4,8","4,7","4,6","5,0","4,9","4,7","4,8","4,6"];

const CATALOGUE = CATALOGUE_BASE.map((c, i) => ({
  ...c,
  slug: "cat" + i,
  price: CAT_PRICE[i] || "200 Gdes",
  count: CAT_COUNT[i] || 50,
  rating: CAT_RATE[i] || "4,7",
}));

const ZONES_HAITI = ["Port-au-Prince","Pétion-Ville","Delmas","Carrefour","Tabarre","Cité Soleil","Croix-des-Bouquets","Kenscoff","Thomassin","Croix-des-Missions","Cap-Haïtien","Les Cayes","Gonaïves","Saint-Marc","Jacmel","Jérémie","Port-de-Paix","Hinche","Fort-Liberté","Miragoâne","Léogâne","Petit-Goâve","Grand-Goâve","Limbé","Ouanaminthe","Mirebalais"];

const FAQ_BASE = [
  { q: "Comment réserver un professionnel ?", a: "Recherchez le service souhaité, comparez les profils et avis, choisissez un créneau puis confirmez. Vous payez en toute sécurité via la plateforme." },
  { q: "Les professionnels sont-ils vérifiés ?", a: "Oui. Chaque professionnel passe par une vérification d'identité, de compétences et de pièces justificatives avant d'être actif sur Kolabor." },
  { q: "Quels moyens de paiement acceptez-vous ?", a: "Nous acceptons les cartes bancaires (Visa, Mastercard) et MonCash. Le paiement est sécurisé et libéré au professionnel une fois le service rendu." },
  { q: "Puis-je annuler une réservation ?", a: "Oui, vous pouvez annuler depuis « Mes réservations ». L'annulation est gratuite jusqu'à 24h avant l'intervention." },
  { q: "Comment devenir professionnel sur Kolabor ?", a: "Cliquez sur « Devenir pro », créez votre profil professionnel avec votre métier, votre zone et votre pièce d'identité. Après validation, vous recevez des demandes." },
];

const JOURS_BASE = [
  { name: "Lundi", open: true }, { name: "Mardi", open: true }, { name: "Mercredi", open: true },
  { name: "Jeudi", open: true }, { name: "Vendredi", open: true }, { name: "Samedi", open: true }, { name: "Dimanche", open: false },
];

const ADMIN_USERS_BASE = [
  { name: "Sophie Martin", email: "sophie.martin@email.ht", role: "Client", city: "Delmas", status: "Actif", color: "#139356", initials: "SM" },
  { name: "Marc Fontaine", email: "marc.fontaine@email.ht", role: "Professionnel", city: "Pétion-Ville", status: "Vérifié", color: "#19355F", initials: "MF" },
  { name: "Ricardo Joseph", email: "ricardo.j@email.ht", role: "Client", city: "Carrefour", status: "Actif", color: "#7C3AED", initials: "RJ" },
  { name: "Naïka Joseph", email: "naika.joseph@email.ht", role: "Professionnel", city: "Delmas", status: "En attente", color: "#0F7A48", initials: "NJ" },
  { name: "Gladys Charles", email: "gladys.c@email.ht", role: "Professionnel", city: "Cap-Haïtien", status: "Vérifié", color: "#EC4899", initials: "GC" },
  { name: "Wesley Dorvil", email: "wesley.d@email.ht", role: "Client", city: "Port-au-Prince", status: "Suspendu", color: "#2563EB", initials: "WD" },
];

const CONVS_BASE = [
  { name: "Marc Fontaine", initials: "MF", color: "#19355F", online: true, job: "Plombier certifié", last: "Parfait, je serai là à 14h.", time: "14:32", unread: 0, messages: [
    { me: false, text: "Bonjour, j'ai une fuite sous l'évier de la cuisine. Êtes-vous disponible aujourd'hui ?", time: "14:20" },
    { me: true, text: "Bonjour ! Oui, je peux passer cet après-midi. Pouvez-vous m'envoyer une photo de la fuite ?", time: "14:24" },
    { me: false, image: true, time: "14:28" },
    { me: true, text: "Je vois, c'est un joint à remplacer. Comptez 250 Gdes. Je serai là à 14h.", time: "14:30" },
    { me: false, text: "Parfait, merci beaucoup !", time: "14:32" },
  ] },
  { name: "Naïka Joseph", initials: "NJ", color: "#0F7A48", online: true, job: "Électricienne", last: "Je vous envoie le devis.", time: "Hier", unread: 2, messages: [
    { me: false, text: "Bonjour, pouvez-vous installer 3 prises dans mon salon ?", time: "09:10" },
    { me: true, text: "Bonjour, bien sûr. Je prépare un devis.", time: "09:32" },
    { me: true, text: "Je vous envoie le devis.", time: "09:33" },
  ] },
  { name: "Roselène Pierre", initials: "RP", color: "#7C3AED", online: false, job: "Aide-ménagère", last: "Merci pour votre confiance !", time: "Lun", unread: 0, messages: [
    { me: false, text: "Le ménage est terminé, tout est impeccable.", time: "Lun 16:40" },
    { me: true, text: "Merci beaucoup, à très bientôt !", time: "Lun 16:45" },
    { me: false, text: "Merci pour votre confiance !", time: "Lun 16:46" },
  ] },
  { name: "Wesley Dorvil", initials: "WD", color: "#2563EB", online: false, job: "Climatisation", last: "D'accord, à bientôt.", time: "28 déc", unread: 0, messages: [
    { me: false, text: "L'entretien du climatiseur est planifié pour demain 10h.", time: "28 déc" },
    { me: true, text: "D'accord, à bientôt.", time: "28 déc" },
  ] },
];

function buildCalDays() {
  const days = [];
  [0, 1, 2].forEach(() => days.push({ label: "", style: {} }));
  for (let n = 1; n <= 31; n++) {
    let style;
    if (n < 8) style = { color: "#D1D5DB" };
    else if (n === 8) style = { color: "#139356", border: "1.5px solid #139356", cursor: "pointer" };
    else if (n === 15) style = { background: "#139356", color: "#fff", cursor: "pointer" };
    else style = { color: "#374151", cursor: "pointer" };
    days.push({
      id: "d" + n,
      label: String(n),
      style: { aspectRatio: "1", display: "flex", alignItems: "center", justifyContent: "center", borderRadius: 10, fontSize: 14, fontWeight: 600, ...style },
    });
  }
  return days;
}

// ---- provider ----

function AppProvider({ children }) {
  const path = useHashPath();
  const screen = screenForPath(path);

  const [signupRole, setSignupRole] = React.useState("client");
  const [catFilter, setCatFilterState] = React.useState("Tous");
  const [faqOpen, setFaqOpen] = React.useState(0);
  const [prosSearch, setProsSearch] = React.useState("");
  const [prosCity, setProsCity] = React.useState("");
  const [appliedQ, setAppliedQ] = React.useState("");
  const [appliedCity, setAppliedCity] = React.useState("");
  const [filterCats, setFilterCats] = React.useState({});
  const [filterRating, setFilterRating] = React.useState(0);
  const [filterAvail, setFilterAvail] = React.useState(false);
  const [filterVerified, setFilterVerified] = React.useState(false);
  const [heroSearch, setHeroSearch] = React.useState("");
  const [heroCity, setHeroCity] = React.useState("");
  const [activeConvIdx, setActiveConvIdx] = React.useState(0);
  const [draft, setDraft] = React.useState("");
  const [convs, setConvs] = React.useState(CONVS_BASE);

  const go = React.useCallback((key) => navigateTo(key), []);

  const mk = (key) => () => go(key);

  const nav = React.useMemo(() => ({
    accueil: mk("accueil"), services: mk("services"), pros: mk("pros"),
    profil: mk("profil"), service: mk("service"), comment: mk("comment"),
    login: mk("login"), signup: mk("signup"), reserver: mk("reserver"),
    paiement: mk("paiement"), confirm: mk("confirm"),
    dashclient: mk("dashclient"), messages: mk("messages"),
    dashpro: mk("dashpro"), admin: mk("admin"), catalogue: mk("catalogue"),
    prolanding: mk("prolanding"),
    tousServices: () => { setCatFilterState("Tous"); go("catalogue"); },
    apropos: mk("apropos"), tarifs: mk("tarifs"), blog: mk("blog"),
    aide: mk("aide"), contact: mk("contact"), faq: mk("faq"), legal: mk("legal"),
    notfound: mk("notfound"), favoris: mk("favoris"), factures: mk("factures"),
    creerCompte: () => go(signupRole === "pro" ? "dashpro" : "confirm"),
    params: mk("params"), messervices: mk("messervices"), dispos: mk("dispos"), revenus: mk("revenus"),
    devenirPro: () => { setSignupRole("pro"); go("signup"); },
    heroSearch: () => doHeroSearch(),
    goCat: {
      Plomberie: () => goProCat("Plomberie"),
      Electricite: () => goProCat("Électricité"),
      Menage: () => goProCat("Ménage"),
      Climatisation: () => goProCat("Climatisation"),
    },
    filtrer: {
      Plomberie: () => setCatFilterAndGo("Plomberie"),
      Electricite: () => setCatFilterAndGo("Électricité"),
      Menage: () => setCatFilterAndGo("Entretien de la maison"),
      Jardinage: () => setCatFilterAndGo("Jardinage"),
    },
    // eslint-disable-next-line
  }), [signupRole]);

  function setCatFilterAndGo(name) {
    setCatFilterState(name);
    go("catalogue");
  }

  function doHeroSearch() {
    const q = (heroSearch || "").trim();
    const c = (heroCity || "").trim();
    setAppliedQ(q); setAppliedCity(c);
    setProsSearch(q); setProsCity(c);
    setFilterCats({}); setFilterRating(0); setFilterAvail(false); setFilterVerified(false);
    go("pros");
  }

  function goProCat(cat) {
    setFilterCats({ [cat]: true });
    setFilterRating(0); setFilterAvail(false); setFilterVerified(false);
    setAppliedQ(""); setAppliedCity(""); setProsSearch(""); setProsCity("");
    go("pros");
  }

  function applyProsSearch() {
    setAppliedQ((prosSearch || "").trim());
    setAppliedCity((prosCity || "").trim());
  }

  function toggleCat(c) {
    setFilterCats((f) => {
      const next = { ...f };
      if (next[c]) delete next[c]; else next[c] = true;
      return next;
    });
  }

  function setRating(r) {
    setFilterRating((cur) => (cur === r ? 0 : r));
  }

  function resetFilters() {
    setFilterCats({}); setFilterRating(0); setFilterAvail(false); setFilterVerified(false);
    setAppliedQ(""); setAppliedCity(""); setProsSearch(""); setProsCity("");
  }

  function toggleFaq(i) {
    setFaqOpen((cur) => (cur === i ? -1 : i));
  }

  function setConv(i) { setActiveConvIdx(i); }
  function onDraft(e) { setDraft(e.target.value); }
  function sendMsg() {
    const d = (draft || "").trim();
    if (!d) return;
    setConvs((cs) => cs.map((c, i) => (i === activeConvIdx ? { ...c, last: d, time: "maintenant", messages: [...c.messages, { me: true, text: d, time: "maintenant" }] } : c)));
    setDraft("");
  }
  function onKey(e) { if (e.key === "Enter") sendMsg(); }

  // ---- derived data (équivalent de renderVals()) ----

  const allPros = React.useMemo(() => {
    const list = PROS_BASE.concat(PROS_MORE).map((p) => ({ ...p, open: () => go("profil") }));
    return list;
  }, []);

  const pros = React.useMemo(() => PROS_BASE.map((p) => ({ ...p, open: () => go("profil") })), []);
  const featured = pros;

  const services = React.useMemo(() => SERVICES.map((sv) => ({ ...sv, open: () => go("service") })), []);

  const prosFiltered = React.useMemo(() => {
    const q = normalize(appliedQ), city = normalize(appliedCity);
    const cats = Object.keys(filterCats);
    const rmin = filterRating;
    return allPros.filter((p) => {
      if (q && !(normalize(p.job).includes(q) || normalize(p.cat).includes(q))) return false;
      if (city && !normalize(p.city).includes(city)) return false;
      if (cats.length && !cats.includes(p.cat)) return false;
      if (rmin && p.ratingNum < rmin) return false;
      if (filterAvail && !p.available) return false;
      if (filterVerified && !p.verified) return false;
      return true;
    });
  }, [allPros, appliedQ, appliedCity, filterCats, filterRating, filterAvail, filterVerified]);

  const prosHeading = React.useMemo(() => {
    const q = (appliedQ || "").trim(), city = (appliedCity || "").trim();
    if (q && city) return q + " à " + city;
    if (q) return q;
    if (city) return "Professionnels à " + city;
    return "Tous les professionnels";
  }, [appliedQ, appliedCity]);

  const filterCatUI = React.useMemo(() => (
    ["Plomberie", "Électricité", "Ménage", "Climatisation", "Jardinage"].map((c) => ({
      id: c,
      name: c,
      checked: !!filterCats[c],
      toggle: () => toggleCat(c),
      boxStyle: filterCats[c]
        ? { width: 20, height: 20, borderRadius: 6, background: "#139356", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }
        : { width: 20, height: 20, borderRadius: 6, border: "1.5px solid #D1D5DB", flexShrink: 0 },
    }))
  ), [filterCats]);

  const radioStyle = (active) => (active
    ? { width: 20, height: 20, borderRadius: "50%", border: "5px solid #139356", boxShadow: "inset 0 0 0 2px #fff", flexShrink: 0 }
    : { width: 20, height: 20, borderRadius: "50%", border: "1.5px solid #D1D5DB", flexShrink: 0 });
  const radio4Style = radioStyle(filterRating === 4);
  const radio3Style = radioStyle(filterRating === 3);

  const trackStyle = (on) => ({ width: 42, height: 24, borderRadius: 999, position: "relative", flexShrink: 0, cursor: "pointer", background: on ? "#139356" : "#E5E7EB" });
  const knobStyle = (on) => ({ position: "absolute", top: 3, width: 18, height: 18, borderRadius: "50%", background: "#fff", ...(on ? { right: 3 } : { left: 3 }) });

  const availTrackStyle = trackStyle(filterAvail);
  const availKnobStyle = knobStyle(filterAvail);
  const verifTrackStyle = trackStyle(filterVerified);
  const verifKnobStyle = knobStyle(filterVerified);

  const roleStyle = (active) => ({
    flex: 1, padding: 12, borderRadius: 10, fontWeight: 700, fontSize: 14,
    background: active ? "#19355F" : "transparent",
    color: active ? "#fff" : "#6B7280",
  });
  const roleClientStyle = roleStyle(signupRole === "client");
  const roleProStyle = roleStyle(signupRole === "pro");

  const jours = React.useMemo(() => JOURS_BASE.map((j) => ({
    id: j.name,
    name: j.name, open: j.open, closed: !j.open,
    toggleStyle: { width: 44, height: 26, borderRadius: 999, position: "relative", flexShrink: 0, background: j.open ? "#139356" : "#E5E7EB" },
    knobStyle: { position: "absolute", top: 3, width: 20, height: 20, borderRadius: "50%", background: "#fff", ...(j.open ? { right: 3 } : { left: 3 }) },
  })), []);

  const adminUsers = React.useMemo(() => ADMIN_USERS_BASE.map((u) => ({
    ...u,
    id: u.email,
    statusStyle: {
      display: "inline-flex", alignItems: "center", gap: 5, padding: "4px 10px", borderRadius: 999, fontSize: 12, fontWeight: 700,
      ...(u.status === "Vérifié" ? { background: "#D8F3E4", color: "#0B5C36" }
        : u.status === "Actif" ? { background: "#DBEAFE", color: "#1E40AF" }
        : u.status === "En attente" ? { background: "#FEF3C7", color: "#B45309" }
        : { background: "#FEE2E2", color: "#B91C1C" }),
    },
    roleStyle: { fontSize: 13, fontWeight: 600, color: u.role === "Professionnel" ? "#139356" : "#6B7280" },
  })), []);

  const metiers = React.useMemo(() => CATALOGUE.map((c) => c.name), []);

  const faqList = React.useMemo(() => FAQ_BASE.map((f, i) => ({
    id: i,
    q: f.q, a: f.a,
    isOpen: faqOpen === i,
    toggle: () => toggleFaq(i),
    iconRot: faqOpen === i ? { transform: "rotate(45deg)", transition: ".2s" } : { transition: ".2s" },
  })), [faqOpen]);

  const catFilters = React.useMemo(() => ["Tous"].concat(CATALOGUE.map((c) => c.name)), []);
  const catFilterList = React.useMemo(() => catFilters.map((f) => ({
    id: f,
    label: f,
    chipStyle: {
      display: "inline-flex", alignItems: "center", whiteSpace: "nowrap", padding: "9px 18px", borderRadius: 999,
      fontSize: 14, fontWeight: f === catFilter ? 700 : 600, cursor: "pointer",
      ...(f === catFilter ? { background: "#19355F", color: "#fff", border: "1.5px solid #19355F" } : { background: "#fff", color: "#374151", border: "1px solid #E5E7EB" }),
    },
    pick: () => setCatFilterState(f),
  })), [catFilters, catFilter]);

  const filteredCatalogue = React.useMemo(() => (
    catFilter === "Tous" ? CATALOGUE : CATALOGUE.filter((c) => c.name === catFilter)
  ), [catFilter]);

  const calDays = React.useMemo(() => buildCalDays(), []);

  const convList = React.useMemo(() => convs.map((c, i) => ({
    ...c,
    id: c.name,
    rowStyle: {
      display: "flex", alignItems: "center", gap: 12, padding: "14px 20px", cursor: "pointer",
      ...(i === activeConvIdx ? { background: "#F0FBF5", borderLeft: "3px solid #139356" } : { borderLeft: "3px solid transparent" }),
    },
    pick: () => setConv(i),
  })), [convs, activeConvIdx]);

  const activeConv = React.useMemo(() => {
    const a = convs[activeConvIdx] || convs[0];
    return {
      ...a,
      statusText: a.online ? "En ligne" : "Hors ligne",
      statusColor: a.online ? "#0B5C36" : "#9CA3AF",
      msgs: a.messages.map((m, i) => ({
        ...m,
        id: i,
        rowStyle: { display: "flex", justifyContent: m.me ? "flex-end" : "flex-start" },
        bubbleStyle: m.image
          ? { maxWidth: "62%" }
          : { maxWidth: "62%", padding: "12px 16px", lineHeight: 1.5, fontSize: 14.5, borderRadius: m.me ? "16px 16px 4px 16px" : "16px 16px 16px 4px", background: m.me ? "#139356" : "#fff", color: m.me ? "#fff" : "#374151", border: m.me ? "none" : "1px solid #F3F4F6" },
        timeStyle: { fontSize: 11, marginTop: 4, display: "block", textAlign: m.me ? "right" : "left", color: m.me ? "rgba(255,255,255,.75)" : "#9CA3AF" },
      })),
    };
  }, [convs, activeConvIdx]);

  const navColor = (k) => (screen === k ? "#19355F" : "#4B5563");

  const isFlags = {
    isAccueil: screen === "accueil", isServices: screen === "services", isPros: screen === "pros",
    isProfil: screen === "profil", isService: screen === "service", isComment: screen === "comment",
    isLogin: screen === "login", isSignup: screen === "signup", isReserver: screen === "reserver",
    isPaiement: screen === "paiement", isConfirm: screen === "confirm",
    isDashClient: screen === "dashclient", isMessages: screen === "messages",
    isDashPro: screen === "dashpro", isAdmin: screen === "admin",
    isCatalogue: screen === "catalogue", isProLanding: screen === "prolanding",
    isApropos: screen === "apropos", isTarifs: screen === "tarifs", isBlog: screen === "blog",
    isAide: screen === "aide", isContact: screen === "contact", isFaq: screen === "faq", isLegal: screen === "legal",
    isNotFound: screen === "notfound", isFavoris: screen === "favoris", isFactures: screen === "factures",
    isParams: screen === "params", isMesServices: screen === "messervices", isDispos: screen === "dispos", isRevenus: screen === "revenus",
  };

  const value = {
    calDays,
    isRoleClient: signupRole === "client",
    isRolePro: signupRole === "pro",
    setRoleClient: () => setSignupRole("client"),
    setRolePro: () => setSignupRole("pro"),
    roleClientStyle, roleProStyle,
    services, allPros,
    screen,
    ...isFlags,
    catFilter,
    prosSearch, prosCity,
    onProsSearch: (e) => setProsSearch(e.target.value),
    onProsCity: (e) => setProsCity(e.target.value),
    heroSearchVal: heroSearch, heroCityVal: heroCity,
    onHeroSearch: (e) => setHeroSearch(e.target.value),
    onHeroCity: (e) => setHeroCity(e.target.value),
    onHeroKey: (e) => { if (e.key === "Enter") doHeroSearch(); },
    applyProsSearch,
    onProsKey: (e) => { if (e.key === "Enter") applyProsSearch(); },
    resetFilters,
    filteredPros: prosFiltered,
    prosCount: prosFiltered.length,
    noPros: prosFiltered.length === 0,
    prosHeading,
    filterCatUI, radio4Style, radio3Style,
    setRating4: () => setRating(4), setRating3: () => setRating(3),
    availTrackStyle, availKnobStyle, verifTrackStyle, verifKnobStyle,
    toggleAvail: () => setFilterAvail((v) => !v),
    toggleVerified: () => setFilterVerified((v) => !v),
    jours, adminUsers, metiers, zonesHaiti: ZONES_HAITI, faqList,
    catFilters, catFilterList, filteredCatalogue, catalogue: CATALOGUE,
    convList, activeConv,
    draft, onDraft, onKey, sendMsg,
    nav,
    navAccueil: navColor("accueil"),
    navServices: (screen === "catalogue" || screen === "services") ? "#19355F" : "#4B5563",
    navPros: navColor("pros"),
    pros, featured,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export { AppContext, AppProvider, useApp };
