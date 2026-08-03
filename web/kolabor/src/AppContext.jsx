import React from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { useHashPath, screenForPath, navigateTo } from "./router.jsx";
import * as servicesApi from "./api/services.js";
import * as prestatairesApi from "./api/prestataires.js";
import * as usersApi from "./api/users.js";
import * as reservationsApi from "./api/reservations.js";
import * as adminApi from "./api/admin.js";
import { field, getUserId, toArray } from "./utils/field.js";
import { useAuth } from "./AuthContext.jsx";

const CATEGORY_PALETTE = ["#7C3AED", "#0EA5E9", "#19355F", "#F59E0B", "#EC4899", "#139356", "#92400E", "#6B7280", "#2563EB", "#0F7A48", "#DC2626", "#4F46E5", "#65A30D", "#B91C1C", "#0891B2", "#1E293B", "#16A34A", "#06B6D4", "#475569", "#DB2777", "#CA8A04", "#7C2D12", "#0D9488", "#EA580C"];
function colorForIndex(i) {
  const n = Number(i);
  return CATEGORY_PALETTE[(Number.isFinite(n) ? n : 0) % CATEGORY_PALETTE.length];
}

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

// `services` et `catalogue` (catégories) viennent maintenant en direct de
// GET /api/services — plus de données statiques ici.

// Liste fixe des métiers proposés à l'inscription (et des catégories de filtre
// sur la recherche de pros), pour garantir que ces 5 métiers apparaissent
// toujours, indépendamment du contenu réel de la table `service`.
const METIERS_PRO = ["Plomberie", "Électricité", "Ménage", "Climatisation", "Jardinage"];

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
  const [selectedServiceId, setSelectedServiceId] = React.useState(null);
  const [selectedProId, setSelectedProId] = React.useState(null);
  const [selectedReservationId, setSelectedReservationId] = React.useState(null);
  const [selectedReservationMontant, setSelectedReservationMontant] = React.useState(0);
  // IDs des réservations créées lors d'une validation de panier (plusieurs
  // services en une fois) — utilisé par Paiement/Confirmation en plus du
  // couple selectedReservationId/selectedReservationMontant pour le flux
  // "un seul service à la fois" existant.
  const [selectedReservationIds, setSelectedReservationIds] = React.useState([]);
  const [checkoutSummary, setCheckoutSummary] = React.useState([]);
  const [panierCheckoutError, setPanierCheckoutError] = React.useState("");
  const [panierCheckoutPending, setPanierCheckoutPending] = React.useState(false);

  // ---- Panier (réservation de plusieurs services en une fois) ----
  // Persisté en localStorage pour survivre à un rafraîchissement de page ;
  // volontairement pas lié à un compte précis (le panier est un état de
  // navigation, pas une donnée serveur).
  const [panier, setPanier] = React.useState(() => {
    try {
      const raw = window.localStorage.getItem("kolabor_panier");
      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  });

  React.useEffect(() => {
    try {
      window.localStorage.setItem("kolabor_panier", JSON.stringify(panier));
    } catch {
      // stockage indisponible (navigation privée, quota...) : on continue
      // silencieusement avec un panier en mémoire seulement.
    }
  }, [panier]);

  function ajouterAuPanier(item) {
    const cartItemId = `${item.prestataireId}-${item.serviceId}-${item.dateHeure}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    setPanier((cur) => [...cur, { ...item, cartItemId }]);
    return cartItemId;
  }

  function retirerDuPanier(cartItemId) {
    setPanier((cur) => cur.filter((it) => it.cartItemId !== cartItemId));
  }

  function viderPanier() {
    setPanier([]);
  }

  const panierTotal = React.useMemo(() => (
    panier.reduce((sum, it) => sum + (Number(it.montant) || 0), 0)
  ), [panier]);
  const panierCount = panier.length;

  const { isAuthenticated, isClient, isPro: isProRole, isAdmin: isAdminRole, userId: authUserId, user: authUser } = useAuth();

  // Valide le panier : crée une réservation par article (le backend n'a pas
  // d'endpoint de réservation groupée), en boucle séquentielle pour rester
  // simple et pouvoir isoler les échecs individuels (créneau déjà pris,
  // etc.) sans faire échouer tout le panier.
  async function validerPanier() {
    if (!authUserId) {
      setPanierCheckoutError("Vous devez être connecté pour réserver.");
      return;
    }
    if (panier.length === 0) return;
    setPanierCheckoutPending(true);
    setPanierCheckoutError("");
    const succeeded = [];
    const failed = [];
    for (const item of panier) {
      try {
        const payload = {
          clientId: authUserId,
          prestataireId: item.prestataireId,
          serviceId: item.serviceId,
          dateHeure: item.dateHeure,
          adresse: item.adresse,
          montant: item.montant,
        };
        // eslint-disable-next-line no-await-in-loop
        const created = await reservationsApi.createReservation(payload);
        const resId = created?.identifiant ?? created?.id;
        succeeded.push({
          cartItemId: item.cartItemId,
          reservationId: resId,
          serviceTitle: item.serviceTitle,
          proName: item.proName,
          montant: item.montant,
        });
      } catch (err) {
        // Le backend renvoie ses erreurs sous la forme {error: "..."} (parfois
        // {message: "..."} pour les erreurs de validation @Valid). On essaie
        // ces deux clés, puis n'importe quelle valeur texte du corps, avant
        // de retomber sur le statut HTTP — pour ne jamais afficher un message
        // générique quand la vraie raison (créneau déjà pris, pro non
        // disponible à cette heure, etc.) est disponible.
        const status = err?.response?.status;
        const data = err?.response?.data;
        let msg = null;
        if (typeof data === "string" && data.trim()) {
          msg = data.trim();
        } else if (data && typeof data === "object") {
          if (typeof data.error === "string") msg = data.error;
          else if (typeof data.message === "string") msg = data.message;
          else {
            const strings = Object.values(data).filter((v) => typeof v === "string");
            if (strings.length) msg = strings.join(" ");
          }
        }
        if (!msg) {
          msg = status ? `Erreur ${status} du serveur.` : (err?.message || "Impossible de contacter le serveur.");
        }
        failed.push({ cartItemId: item.cartItemId, serviceTitle: item.serviceTitle, proName: item.proName, error: msg });
      }
    }
    setPanierCheckoutPending(false);
    if (succeeded.length > 0) {
      const succeededIds = new Set(succeeded.map((s) => s.cartItemId));
      setPanier((cur) => cur.filter((it) => !succeededIds.has(it.cartItemId)));
      setCheckoutSummary(succeeded);
      setSelectedReservationIds(succeeded.map((s) => s.reservationId).filter(Boolean));
      setSelectedReservationMontant(succeeded.reduce((sum, s) => sum + (Number(s.montant) || 0), 0));
    }
    if (failed.length > 0) {
      setPanierCheckoutError(
        `${failed.length} service${failed.length > 1 ? "s" : ""} n'${failed.length > 1 ? "ont" : "a"} pas pu être réservé${failed.length > 1 ? "s" : ""} : ` +
        failed.map((f) => `${f.serviceTitle} avec ${f.proName} (${f.error})`).join(" · ")
      );
    }
    if (succeeded.length > 0) {
      // Sans ce refetch, la nouvelle réservation n'apparaît jamais dans "Mes
      // réservations" tant que la page n'est pas rechargée manuellement :
      // reservationsClientQuery ne se charge qu'une fois au montage de
      // l'app, et la navigation entre écrans ne remonte pas AppProvider.
      reservationsClientQuery.refetch();
    }
    if (succeeded.length > 0 && failed.length === 0) {
      navigateTo("paiement");
    }
  }

  const meQuery = useQuery({
    queryKey: ["users", authUserId],
    queryFn: () => usersApi.getUser(authUserId),
    enabled: isAuthenticated && !!authUserId,
  });
  const me = React.useMemo(() => {
    const u = meQuery.data;
    // Le nom (et les autres infos de base) sont déjà présents dans la réponse
    // de login/register — on les utilise en attendant que le profil complet
    // (GET /users/{id}) se charge, pour ne jamais afficher un nom générique
    // "Utilisateur" à la place du vrai nom de la personne connectée.
    const nom = field(u, "nom", "name") || field(authUser, "nom", "name") || "";
    const initials = nom.split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase();
    return {
      nom,
      initials: initials || "",
      ville: field(u, "zoneIntervention") || field(u, "adresseParDefaut") || "",
      email: field(u, "e-mail", "email") || field(authUser, "e-mail", "email") || "",
      telephone: field(u, "téléphone", "telephone") || field(authUser, "téléphone", "telephone") || "",
      tarifHoraire: field(u, "tarifHoraire") ?? "",
      competences: field(u, "compétences", "competences") || "",
      disponible: !!field(u, "disponible"),
      zoneIntervention: field(u, "zoneIntervention") || "",
      bio: field(u, "bio") || "",
      // NB: `field(u, "photo")` (l'URL publique /uploads/... renvoyée par le
      // backend au moment de l'upload) est utilisée ici plutôt que
      // `usersApi.getUserPhotoUrl(id)` (GET /users/{id}/photo). Ce dernier
      // endpoint exige un Bearer token, or un <img src="..."> ne peut pas
      // envoyer d'en-tête d'autorisation : la requête échouait donc toujours
      // en 401 et la photo ne s'affichait jamais, même après un upload réussi.
      photoUrl: field(u, "photo") || "",
    };
  }, [meQuery.data, authUserId, authUser]);

  const uploadPhotoMutation = useMutation({
    mutationFn: (file) => usersApi.uploadUserPhoto(authUserId, file),
    onSuccess: () => meQuery.refetch(),
  });

  // Un compte PRESTATAIRE passe par PUT /api/prestataires/{id}, qui gère
  // aussi bien les champs de base (nom, téléphone) que les champs propres au
  // pro (bio, compétences, tarif, zone). Un compte CLIENT n'a pas ces
  // champs-là : il passe par le PUT /api/users/{id} générique, qui n'existe
  // que pour nom/téléphone mais fonctionne pour n'importe quel rôle.
  const updateProfileMutation = useMutation({
    mutationFn: (payload) => (
      isProRole
        ? prestatairesApi.updateProfile(authUserId, payload)
        : usersApi.updateProfile(authUserId, payload)
    ),
    onSuccess: () => meQuery.refetch(),
  });

  const fcmTokenMutation = useMutation({
    mutationFn: (token) => usersApi.updateFcmToken(token),
  });

  const servicesQuery = useQuery({
    queryKey: ["services"],
    queryFn: servicesApi.getServices,
  });
  const rawServices = React.useMemo(() => toArray(servicesQuery.data), [servicesQuery.data]);

  const go = React.useCallback((key) => navigateTo(key), []);

  // GET /api/services — le catalogue plat vient directement de l'API.
  // (Défini tôt car `selectedService`, `metiers`, etc. en dépendent plus bas.)
  const services = React.useMemo(() => rawServices.map((sv, i) => {
    const id = field(sv, "identifiant", "id");
    return {
      id,
      cat: field(sv, "catégorie", "categorie", "category") || "Autre",
      title: field(sv, "nom", "name") || "Service",
      description: field(sv, "description") || "",
      tag: colorForIndex(i),
      open: () => { setSelectedServiceId(id); go("service"); },
    };
  }), [rawServices]);

  // Catégories dérivées des services réels (regroupement côté client, car
  // l'API ne renvoie qu'une liste plate de services).
  const catalogue = React.useMemo(() => {
    const groups = [];
    const index = new Map();
    rawServices.forEach((sv) => {
      const name = field(sv, "catégorie", "categorie", "category") || "Autre";
      if (!index.has(name)) {
        index.set(name, groups.length);
        groups.push({ name, items: [] });
      }
      groups[index.get(name)].items.push(field(sv, "nom", "name") || "Service");
    });
    return groups.map((g, i) => ({
      name: g.name,
      slug: "cat" + i,
      color: colorForIndex(i),
      bg: "#F3F4F6",
      count: g.items.length,
      items: g.items,
      open: () => { setCatFilterState(g.name); go("services"); },
    }));
  }, [rawServices]);

  const mk = (key) => () => go(key);

  const nav = React.useMemo(() => ({
    accueil: mk("accueil"), services: mk("services"), pros: mk("pros"),
    service: mk("service"), comment: mk("comment"),
    login: mk("login"), signup: mk("signup"), reserver: mk("reserver"),
    panier: mk("panier"),
    paiement: mk("paiement"), confirm: mk("confirm"),
    dashclient: mk("dashclient"),
    dashpro: mk("dashpro"), administration: mk("administration"), catalogue: mk("catalogue"),
    prolanding: mk("prolanding"),
    tousServices: () => { setCatFilterState("Tous"); go("catalogue"); },
    apropos: mk("apropos"), tarifs: mk("tarifs"), blog: mk("blog"),
    aide: mk("aide"), contact: mk("contact"), faq: mk("faq"), legal: mk("legal"),
    notfound: mk("notfound"), factures: mk("factures"),
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
      Menage: () => setCatFilterAndGo("Ménage"),
      Jardinage: () => setCatFilterAndGo("Jardinage"),
    },
    // eslint-disable-next-line
  }), [signupRole]);

  function setCatFilterAndGo(name) {
    setCatFilterState(name);
    go("catalogue");
  }

  // Utilisé par la grille "Explorez par catégorie" de l'accueil : clique sur
  // une catégorie précise doit amener exactement aux services de cette
  // catégorie sur /services (auparavant tous les boutons pointaient vers
  // /services sans jamais préciser la catégorie).
  function goToServiceCategory(name) {
    setCatFilterState(name);
    go("services");
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

  // GET /api/prestataires/recherche — recherche publique (l'API accepte des
  // filtres optionnels ; on lui envoie le texte et la ville recherchés, et on
  // affine ensuite côté client avec les filtres de catégorie/note/dispo qui ne
  // sont pas garantis côté serveur).
  const prestatairesQuery = useQuery({
    queryKey: ["prestataires", appliedQ, appliedCity],
    queryFn: () => prestatairesApi.rechercherPrestataires({
      service: appliedQ || undefined,
      zone: appliedCity || undefined,
    }),
  });
  const rawPros = React.useMemo(() => toArray(prestatairesQuery.data), [prestatairesQuery.data]);

  const allPros = React.useMemo(() => rawPros.map((p, i) => {
    const id = getUserId(p);
    const nom = field(p, "nom", "name") || "Prestataire";
    const initials = nom.split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase();
    const noteNum = Number(field(p, "moyenneNotes", "note")) || 0;
    return {
      id,
      cat: field(p, "compétences", "competences") || "Service à domicile",
      ratingNum: noteNum,
      priceNum: Number(field(p, "tarifHoraire")) || 0,
      initials: initials || "PR",
      name: nom,
      job: field(p, "compétences", "competences") || "Prestataire",
      city: field(p, "zoneIntervention") || "",
      rating: noteNum ? noteNum.toFixed(1).replace(".", ",") : "—",
      reviews: Number(field(p, "nombreAvis")) || 0,
      price: field(p, "tarifHoraire") ? `${field(p, "tarifHoraire")} Gdes/h` : "Sur devis",
      avatar: colorForIndex(i),
      verified: true,
      available: !!field(p, "disponible"),
      popular: false,
      photoUrl: field(p, "photo") || "",
      bio: field(p, "bio") || "",
      // La page dédiée /#/profil a été retirée : voir le détail d'un pro
      // (et le laisser un avis) se fait maintenant en place sur /pros.
      open: () => { setSelectedProId(id); go("pros"); },
    };
  }), [rawPros]);

  // Un pro non disponible ne doit apparaître dans aucune liste menant à une
  // réservation, y compris les "Professionnels en vedette" de l'accueil.
  const pros = React.useMemo(() => allPros.filter((p) => p.available).slice(0, 4), [allPros]);
  const featured = pros;
  const prosLoading = prestatairesQuery.isLoading;
  const prosError = prestatairesQuery.isError;

  // ---- Détail service (écran ServiceDetail) ----
  const selectedService = React.useMemo(() => (
    services.find((s) => String(s.id) === String(selectedServiceId)) || null
  ), [services, selectedServiceId]);

  // ---- Détail prestataire (écran Profil) ----
  const selectedProFromList = React.useMemo(() => (
    allPros.find((p) => String(p.id) === String(selectedProId)) || null
  ), [allPros, selectedProId]);

  const selectedProUserQuery = useQuery({
    queryKey: ["users", selectedProId],
    queryFn: () => usersApi.getUser(selectedProId),
    enabled: !!selectedProId,
  });

  const selectedProStatsQuery = useQuery({
    queryKey: ["prestataires", selectedProId, "statistiques"],
    queryFn: () => prestatairesApi.getStatistiques(selectedProId),
    enabled: !!selectedProId,
  });

  const selectedProAvisQuery = useQuery({
    queryKey: ["prestataires", selectedProId, "avis"],
    queryFn: () => prestatairesApi.getAvisProfil(selectedProId),
    enabled: !!selectedProId,
  });

  const selectedPro = React.useMemo(() => {
    const u = selectedProUserQuery.data;
    if (!u && !selectedProFromList) return null;
    const base = selectedProFromList || {};
    const nom = field(u, "nom", "name") || base.name || "Prestataire";
    const initials = nom.split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase();
    return {
      id: selectedProId,
      name: nom,
      initials: initials || base.initials || "PR",
      job: field(u, "compétences", "competences") || base.job || "Prestataire",
      city: field(u, "zoneIntervention") || base.city || "",
      price: field(u, "tarifHoraire") || base.priceNum || "",
      rating: field(u, "moyenneNotes") || base.rating || "—",
      reviews: field(u, "nombreAvis") || base.reviews || 0,
      available: field(u, "disponible") ?? base.available ?? false,
      // Voir le commentaire équivalent dans `me` ci-dessus : on utilise l'URL
      // publique stockée en base plutôt que l'endpoint protégé par token,
      // inutilisable comme src d'<img>.
      photoUrl: field(u, "photo") || base.photoUrl || "",
      bio: field(u, "bio") || base.bio || "",
    };
  }, [selectedProUserQuery.data, selectedProFromList, selectedProId]);

  const selectedProStats = selectedProStatsQuery.data || null;
  const selectedProAvis = toArray(selectedProAvisQuery.data);

  // ---- GET /api/reservations/me/client — réservations où l'utilisateur
  // connecté est le client, quel que soit son rôle de compte (un compte
  // PRESTATAIRE peut aussi réserver un service en tant que client) ----
  const reservationsClientQuery = useQuery({
    queryKey: ["reservations", "me", "client"],
    queryFn: reservationsApi.getMesReservationsClient,
    enabled: isAuthenticated,
    // AppProvider (et donc ses queries) ne se remonte jamais en naviguant
    // entre écrans — sans ce polling, une réservation créée ou dont le
    // statut change ailleurs (acceptée/refusée par le pro) n'apparaît/ne se
    // met à jour ici qu'après un rechargement manuel complet de la page.
    refetchInterval: 15000,
  });

  function normalizeReservation(r, i) {
    const id = field(r, "identifiant", "id");
    const prestataire = field(r, "prestataire") || {};
    const client = field(r, "client") || {};
    const service = field(r, "service") || {};
    const statutRaw = (field(r, "statut") || "").toString().toUpperCase();
    return {
      id,
      raw: r,
      titre: field(service, "nom", "name") || "Service",
      prestataireId: field(prestataire, "identifiant", "id"),
      proNom: field(prestataire, "nom", "name") || "Prestataire",
      proJob: field(prestataire, "compétences", "competences") || "",
      clientNom: field(client, "nom", "name") || "Client",
      dateHeure: field(r, "dateHeure") || "",
      adresse: field(r, "adresse") || "",
      statut: statutRaw,
      statutLabel: statutRaw === "ACCEPTEE" ? "Confirmé"
        : statutRaw === "EN_ATTENTE" ? "En attente"
        : statutRaw === "EN_COURS" ? "En cours"
        : statutRaw === "TERMINEE" ? "Terminé"
        : statutRaw === "REFUSEE" ? "Refusé"
        : statutRaw === "ANNULEE" ? "Annulé"
        : statutRaw === "PAYEE" ? "Payé"
        : statutRaw || "—",
      montant: field(r, "montant") || 0,
      key: id ?? i,
    };
  }

  const reservationsClient = React.useMemo(() => (
    toArray(reservationsClientQuery.data).map(normalizeReservation)
  ), [reservationsClientQuery.data]);

  const clientStats = React.useMemo(() => {
    const list = reservationsClient;
    const upcoming = list.filter((r) => r.statut === "ACCEPTEE" || r.statut === "EN_ATTENTE" || r.statut === "EN_COURS" || r.statut === "PAYEE").length;
    const done = list.filter((r) => r.statut === "TERMINEE").length;
    // Une réservation annulée ne doit compter ni dans le total dépensé ni
    // dans aucune autre agrégation basée sur reservationsClient — sinon les
    // chiffres du tableau de bord restent incohérents après une annulation.
    const total = list
      .filter((r) => r.statut !== "ANNULEE")
      .reduce((sum, r) => sum + (Number(r.montant) || 0), 0);
    return { upcoming, done, total };
  }, [reservationsClient]);

  const updateStatutMutation = useMutation({
    mutationFn: ({ id, statut, prestataireId }) => reservationsApi.updateStatut(id, statut, prestataireId),
    onSuccess: () => reservationsClientQuery.refetch(),
  });

  const laisserAvisMutation = useMutation({
    mutationFn: ({ id, note, commentaire, clientId }) => reservationsApi.laisserAvis(id, { note, commentaire }, clientId),
    onSuccess: () => {
      reservationsClientQuery.refetch();
      // Rafraîchit aussi les avis/statistiques du profil affiché, pour que
      // l'avis qu'on vient de laisser depuis la page Profil apparaisse tout
      // de suite sans recharger la page.
      selectedProAvisQuery.refetch();
      selectedProStatsQuery.refetch();
    },
  });

  // Avis laissé directement sur le profil d'un prestataire, sans passer par
  // une réservation terminée (POST /api/prestataires/{id}/avis). N'importe
  // quel compte connecté peut évaluer n'importe quel autre prestataire —
  // seul l'avis sur son propre profil est bloqué (côté backend et UI).
  const laisserAvisProfilMutation = useMutation({
    mutationFn: ({ prestataireId, note, commentaire, clientId }) => (
      prestatairesApi.laisserAvisProfil(prestataireId, { note, commentaire }, clientId)
    ),
    onSuccess: () => {
      selectedProAvisQuery.refetch();
      selectedProStatsQuery.refetch();
    },
  });

  const canReviewSelectedPro = isAuthenticated && !!selectedProId && String(selectedProId) !== String(authUserId);

  function laisserAvisSurProfil(note, commentaire, callbacks) {
    if (!selectedProId || !authUserId) return;
    laisserAvisProfilMutation.mutate(
      {
        prestataireId: selectedProId,
        note,
        commentaire: commentaire || "",
        clientId: authUserId,
      },
      callbacks
    );
  }

  // PUT /reservations/{id}/statut est réservé aux PRESTATAIRES (vérification
  // explicite du rôle + de l'ID prestataire) : un client ne peut pas annuler
  // sa propre réservation via cet endpoint. On utilise donc un endpoint dédié
  // PUT /reservations/{id}/annuler, scoped à l'identité du client.
  const annulerReservationMutation = useMutation({
    mutationFn: ({ id, clientId }) => reservationsApi.annulerReservationClient(id, clientId),
    onSuccess: () => reservationsClientQuery.refetch(),
    onError: (err) => {
      // Sans ce callback, un échec (ex: backend pas encore redémarré avec le
      // nouvel endpoint → 404, ou réservation déjà terminée → 400) passait
      // inaperçu : le bouton "Annuler" semblait "ne rien faire".
      const status = err?.response?.status;
      const data = err?.response?.data;
      let msg = null;
      if (typeof data === "string" && data.trim()) msg = data.trim();
      else if (data && typeof data === "object") {
        if (typeof data.error === "string") msg = data.error;
        else if (typeof data.message === "string") msg = data.message;
      }
      if (!msg) msg = status ? `Erreur ${status} du serveur.` : (err?.message || "Impossible de contacter le serveur.");
      window.alert(`Impossible d'annuler cette réservation : ${msg}`);
    },
  });

  function annulerReservation(id) {
    if (!authUserId) return;
    if (!window.confirm("Annuler cette réservation ?")) return;
    annulerReservationMutation.mutate({ id, clientId: authUserId });
  }

  function laisserAvisSurReservation(id) {
    const noteStr = window.prompt("Votre note sur 5 (1 à 5) ?", "5");
    if (!noteStr) return;
    const note = Number(noteStr);
    if (!note || note < 1 || note > 5) return;
    const commentaire = window.prompt("Un commentaire (optionnel) ?", "") || "";
    laisserAvisMutation.mutate({ id, note, commentaire, clientId: authUserId });
  }

  // ---- Espace prestataire : agenda, statistiques, revenus 7 jours ----
  const agendaProQuery = useQuery({
    queryKey: ["reservations", "me", "prestataire"],
    queryFn: reservationsApi.getMesReservationsPrestataire,
    enabled: isAuthenticated && isProRole,
    // Même raison que reservationsClientQuery : sans polling, une nouvelle
    // demande de réservation créée par un client n'apparaît jamais dans
    // "Demandes reçues" tant que le pro ne recharge pas la page à la main.
    refetchInterval: 15000,
  });

  const reservationsPro = React.useMemo(() => (
    toArray(agendaProQuery.data).map(normalizeReservation)
  ), [agendaProQuery.data]);

  const demandesEnAttente = React.useMemo(() => (
    reservationsPro.filter((r) => r.statut === "EN_ATTENTE")
  ), [reservationsPro]);

  // Polling : les statistiques et le graphe se mettent à jour tout seuls
  // quand un client réserve/paie ailleurs, sans recharger la page.
  const proStatsQuery = useQuery({
    queryKey: ["prestataires", authUserId, "statistiques"],
    queryFn: () => prestatairesApi.getStatistiques(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
    refetchInterval: 15000,
  });

  const proRevenueWeekQuery = useQuery({
    queryKey: ["prestataires", authUserId, "revenue-week"],
    queryFn: () => prestatairesApi.getRevenueWeek(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
    refetchInterval: 15000,
  });

  const proStats = proStatsQuery.data || {};
  const proRevenueWeek = React.useMemo(() => (
    toArray(proRevenueWeekQuery.data).map((d, i) => ({
      jour: field(d, "jour", "day") || ["L", "M", "M", "J", "V", "S", "D"][i] || "",
      montant: Number(field(d, "montant", "revenu", "value", "amount")) || 0,
    }))
  ), [proRevenueWeekQuery.data]);

  const refreshProDashboard = () => {
    agendaProQuery.refetch();
    proStatsQuery.refetch();
    proRevenueWeekQuery.refetch();
  };
  const accepterDemande = (id) => updateStatutMutation.mutate({ id, statut: "ACCEPTEE", prestataireId: authUserId }, { onSuccess: refreshProDashboard });
  const refuserDemande = (id) => updateStatutMutation.mutate({ id, statut: "REFUSEE", prestataireId: authUserId }, { onSuccess: refreshProDashboard });
  // Marquer une prestation comme terminée : elle bascule alors dans les
  // revenus (statut TERMINEE = payé/encaissé) et tout le tableau de bord se
  // rafraîchit immédiatement.
  const terminerPrestation = (id) => updateStatutMutation.mutate({ id, statut: "TERMINEE", prestataireId: authUserId }, { onSuccess: refreshProDashboard });

  // ---- Page "Mes services" (espace pro) : tout vient de la base de données ----
  // Deux sources réelles, fusionnées :
  //   1. les services du catalogue (GET /api/services) dont la catégorie
  //      correspond à la compétence déclarée par CE prestataire ;
  //   2. les services effectivement réservés chez ce prestataire (GET
  //      /api/reservations/me/prestataire) — garantit que tout service qu'un
  //      client a réellement commandé chez lui apparaît, même si la
  //      correspondance compétence/catégorie ne le trouvait pas.
  const mesServices = React.useMemo(() => {
    // La compétence/catégorie du pro connecté est lue depuis DEUX sources en
    // base, pour couvrir tous les comptes : son profil (GET /users/{id}) et,
    // en repli, sa fiche dans l'annuaire public des prestataires (GET
    // /prestataires/recherche) — certains comptes n'exposent leur métier que
    // par l'une des deux routes.
    const fromDirectory = allPros.find((p) => String(p.id) === String(authUserId));
    const competencesEffectives = me.competences || (fromDirectory && fromDirectory.cat) || "";
    // `competences` peut contenir une catégorie ("Plomberie", "Sécurité"), un
    // nom de service précis, ou plusieurs valeurs séparées par des virgules —
    // selon la façon dont le compte a été créé. On découpe puis on compare
    // chaque fragment à la fois à la CATÉGORIE et au TITRE de chaque service
    // du catalogue, pour couvrir tous les formats existants en base.
    const fragments = competencesEffectives
      .split(/[,;\/|]+/)
      .map((x) => normalize(x.trim()))
      .filter(Boolean);
    const matchesPro = (s) => {
      const cat = normalize(s.cat);
      const title = normalize(s.title);
      return fragments.some((f) => (
        cat.includes(f) || f.includes(cat) || title.includes(f) || f.includes(title)
      ));
    };
    const byKey = new Map();
    services.forEach((s) => {
      if (fragments.length && matchesPro(s)) {
        byKey.set(normalize(s.title), s);
      }
    });
    reservationsPro.forEach((r, i) => {
      const k = normalize(r.titre);
      if (!k || byKey.has(k)) return;
      const fromCatalog = services.find((s) => normalize(s.title) === k);
      byKey.set(k, fromCatalog || {
        id: `resa-${r.key ?? i}`,
        cat: competencesEffectives || "Service",
        title: r.titre,
        description: "",
        tag: colorForIndex(byKey.size),
      });
    });
    return Array.from(byKey.values());
  }, [services, me.competences, reservationsPro, allPros, authUserId]);

  // ---- Avis reçus par le prestataire connecté (page Revenus) ----
  const mesAvisQuery = useQuery({
    queryKey: ["prestataires", authUserId, "avis"],
    queryFn: () => prestatairesApi.getAvisProfil(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
    refetchInterval: 30000,
  });
  const mesAvis = React.useMemo(() => (
    toArray(mesAvisQuery.data).map((a, i) => {
      const client = field(a, "client") || {};
      return {
        id: field(a, "identifiant", "id") ?? i,
        note: Number(field(a, "note")) || 0,
        commentaire: field(a, "commentaire") || "",
        date: field(a, "date") || "",
        clientNom: field(client, "nom", "name") || "Client",
        key: field(a, "identifiant", "id") ?? i,
      };
    })
  ), [mesAvisQuery.data]);

  // ---- Disponibilités du prestataire connecté ----
  const disposQuery = useQuery({
    queryKey: ["prestataires", authUserId, "disponibilites"],
    queryFn: () => prestatairesApi.getDisponibilites(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
  });

  const disposList = React.useMemo(() => (
    toArray(disposQuery.data).map((d, i) => ({
      id: field(d, "identifiant", "id"),
      jour: field(d, "jour") || "",
      heureDebut: field(d, "heureDébut", "heureDebut") || "",
      heureFin: field(d, "heureFin") || "",
      key: field(d, "identifiant", "id") ?? i,
    }))
  ), [disposQuery.data]);

  const addDisponibiliteMutation = useMutation({
    mutationFn: (payload) => prestatairesApi.addDisponibilite(authUserId, payload),
    onSuccess: () => disposQuery.refetch(),
  });

  const deleteDisponibiliteMutation = useMutation({
    mutationFn: (disponibiliteId) => prestatairesApi.deleteDisponibilite(disponibiliteId),
    onSuccess: () => disposQuery.refetch(),
  });

  const updateAvailabilityMutation = useMutation({
    mutationFn: (disponible) => prestatairesApi.updateAvailability(authUserId, disponible),
  });

  function ajouterDisponibilite({ jour, heureDebut, heureFin }) {
    addDisponibiliteMutation.mutate({ jour, heureDebut: `${heureDebut}:00`, heureFin: `${heureFin}:00` });
  }

  function supprimerDisponibilite(id) {
    deleteDisponibiliteMutation.mutate(id);
  }

  // ---- Espace admin (/#/administration) : KPIs, revenus, catégories, utilisateurs, litiges ----
  const adminEnabled = isAuthenticated && isAdminRole;

  const adminKpisQuery = useQuery({
    queryKey: ["admin", "kpis"],
    queryFn: adminApi.getKpis,
    enabled: adminEnabled,
    refetchInterval: 30000,
  });
  const adminKpis = adminKpisQuery.data || {};

  const adminRevenusMensuelsQuery = useQuery({
    queryKey: ["admin", "revenus-mensuels"],
    queryFn: adminApi.getRevenusMensuels,
    enabled: adminEnabled,
  });
  const adminRevenusMensuels = React.useMemo(() => (
    toArray(adminRevenusMensuelsQuery.data).map((d) => ({
      mois: field(d, "mois") || "",
      montant: Number(field(d, "montant")) || 0,
    }))
  ), [adminRevenusMensuelsQuery.data]);

  const adminTopCategoriesQuery = useQuery({
    queryKey: ["admin", "top-categories"],
    queryFn: adminApi.getTopCategories,
    enabled: adminEnabled,
  });
  const adminTopCategories = React.useMemo(() => (
    toArray(adminTopCategoriesQuery.data).map((d, i) => ({
      categorie: field(d, "catégorie", "categorie") || "Autre",
      montant: Number(field(d, "montant")) || 0,
      pourcentage: Number(field(d, "pourcentage")) || 0,
      color: colorForIndex(i),
    }))
  ), [adminTopCategoriesQuery.data]);

  const adminUsersQuery = useQuery({
    queryKey: ["admin", "users"],
    queryFn: adminApi.getUsers,
    enabled: adminEnabled,
  });
  const adminUsers = React.useMemo(() => (
    toArray(adminUsersQuery.data).map((u, i) => {
      const role = (field(u, "role") || "").toString().toUpperCase();
      const roleLabel = role === "PRESTATAIRE" ? "Professionnel" : role === "ADMIN" ? "Admin" : "Client";
      const disponible = field(u, "disponible");
      // `statutCompte` (ACTIF/SUSPENDU) reflète une décision de modération de
      // l'admin — distinct de `disponible`, que le prestataire pilote
      // lui-même pour signaler s'il prend de nouvelles missions. Un compte
      // suspendu prime toujours sur l'affichage "Actif / En pause".
      const statutCompte = (field(u, "statutCompte") || "ACTIF").toString().toUpperCase();
      const suspendu = role === "PRESTATAIRE" && statutCompte === "SUSPENDU";
      const status = suspendu ? "Suspendu" : role === "PRESTATAIRE" ? (disponible ? "Actif" : "En pause") : "Actif";
      const nom = field(u, "nom", "name") || "Utilisateur";
      const initials = nom.split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase();
      return {
        id: field(u, "id") ?? i,
        name: nom,
        email: field(u, "email") || "",
        telephone: field(u, "téléphone", "telephone") || "",
        role: roleLabel,
        isPrestataire: role === "PRESTATAIRE",
        statutCompte,
        suspendu,
        status,
        dateInscription: field(u, "dateInscription") || "",
        moyenneNotes: field(u, "moyenneNotes"),
        color: colorForIndex(i),
        initials,
        statusStyle: {
          display: "inline-flex", alignItems: "center", gap: 5, padding: "4px 10px", borderRadius: 999, fontSize: 12, fontWeight: 700,
          ...(status === "Actif" ? { background: "#D8F3E4", color: "#0B5C36" }
            : status === "En pause" ? { background: "#FEF3C7", color: "#B45309" }
            : { background: "#FEE2E2", color: "#B91C1C" }),
        },
        roleStyle: { fontSize: 13, fontWeight: 600, color: roleLabel === "Professionnel" ? "#139356" : roleLabel === "Admin" ? "#7C3AED" : "#6B7280" },
      };
    })
  ), [adminUsersQuery.data]);

  // ---- Admin : valider (réactiver) ou suspendre un compte prestataire ----
  const statutPrestataireMutation = useMutation({
    mutationFn: ({ id, statut }) => adminApi.updateStatutPrestataire(id, statut),
    onSuccess: () => adminUsersQuery.refetch(),
  });

  function suspendrePrestataire(id, nom) {
    const ok = window.confirm(`Suspendre le compte de ${nom || "ce prestataire"} ? Il n'apparaîtra plus dans la recherche publique.`);
    if (!ok) return;
    statutPrestataireMutation.mutate({ id, statut: "SUSPENDU" });
  }

  function validerPrestataire(id) {
    statutPrestataireMutation.mutate({ id, statut: "ACTIF" });
  }

  const litigesOuvertsQuery = useQuery({
    queryKey: ["reservations", "litiges", "ouverts"],
    queryFn: reservationsApi.getLitigesOuverts,
    // Réservé aux ADMIN côté backend (403 sinon) : inutile d'appeler cet
    // endpoint pour chaque client/pro connecté qui n'y a de toute façon pas
    // accès.
    enabled: adminEnabled,
  });

  function mapLitige(l, i) {
    const reservation = field(l, "réservation", "reservation") || {};
    const client = field(reservation, "client") || field(l, "client") || {};
    const prestataire = field(reservation, "prestataire") || field(l, "prestataire") || {};
    return {
      litigeId: field(l, "identifiant", "id", "litigeId"),
      reservationId: field(reservation, "identifiant", "id") || field(l, "reservationId"),
      motif: field(l, "motif") || "",
      resolution: field(l, "resolution") || "",
      clientNom: field(client, "nom", "name") || "Client",
      proNom: field(prestataire, "nom", "name") || "Prestataire",
      statut: field(l, "statut") || "OUVERT",
      key: field(l, "identifiant", "id", "litigeId") ?? i,
    };
  }

  const litigesOuverts = React.useMemo(() => (
    toArray(litigesOuvertsQuery.data).map(mapLitige)
  ), [litigesOuvertsQuery.data]);

  // Historique complet des litiges (tous statuts) — alimente l'onglet "Tous"
  // de l'espace admin, en plus de la vue "Ouverts" par défaut ci-dessus.
  const tousLitigesQuery = useQuery({
    queryKey: ["reservations", "litiges", "tous"],
    queryFn: reservationsApi.getTousLitiges,
    enabled: adminEnabled,
  });
  const tousLitiges = React.useMemo(() => (
    toArray(tousLitigesQuery.data).map(mapLitige)
  ), [tousLitigesQuery.data]);

  const resoudreLitigeMutation = useMutation({
    mutationFn: ({ litigeId, resolution, statut }) => reservationsApi.resoudreLitige(litigeId, resolution, statut),
    onSuccess: () => {
      litigesOuvertsQuery.refetch();
      tousLitigesQuery.refetch();
    },
  });

  function resoudreLitige(litigeId) {
    const resolution = window.prompt("Décision / résolution du litige :", "");
    if (resolution === null) return;
    resoudreLitigeMutation.mutate({ litigeId, resolution, statut: "RESOLU" });
  }

  function rejeterLitige(litigeId) {
    const resolution = window.prompt("Motif du rejet du litige :", "");
    if (resolution === null) return;
    resoudreLitigeMutation.mutate({ litigeId, resolution, statut: "REJETE" });
  }

  const prosFiltered = React.useMemo(() => {
    const q = normalize(appliedQ), city = normalize(appliedCity);
    const cats = Object.keys(filterCats);
    const rmin = filterRating;
    return allPros.filter((p) => {
      if (q && !(normalize(p.job).includes(q) || normalize(p.cat).includes(q))) return false;
      if (city && !normalize(p.city).includes(city)) return false;
      if (cats.length && !cats.includes(p.cat)) return false;
      if (rmin && p.ratingNum < rmin) return false;
      // Un professionnel non disponible ne doit jamais apparaître dans la
      // liste de réservation — ce n'est plus un filtre optionnel.
      if (!p.available) return false;
      if (filterVerified && !p.verified) return false;
      return true;
    });
  }, [allPros, appliedQ, appliedCity, filterCats, filterRating, filterVerified]);

  const prosHeading = React.useMemo(() => {
    const q = (appliedQ || "").trim(), city = (appliedCity || "").trim();
    if (q && city) return q + " à " + city;
    if (q) return q;
    if (city) return "Professionnels à " + city;
    return "Tous les professionnels";
  }, [appliedQ, appliedCity]);

  const filterCatUI = React.useMemo(() => (
    METIERS_PRO.map((c) => ({
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

  // Le Signup (choix du métier du pro) utilise désormais les vraies
  // catégories de la table `service` (via GET /api/services). La liste fixe
  // METIERS_PRO ne sert plus que de secours pendant le chargement ou si la
  // table est vide/l'API est en erreur, pour ne jamais laisser le menu vide.
  const metiers = catalogue.length > 0 ? catalogue.map((c) => c.name) : METIERS_PRO;

  const faqList = React.useMemo(() => FAQ_BASE.map((f, i) => ({
    id: i,
    q: f.q, a: f.a,
    isOpen: faqOpen === i,
    toggle: () => toggleFaq(i),
    iconRot: faqOpen === i ? { transform: "rotate(45deg)", transition: ".2s" } : { transition: ".2s" },
  })), [faqOpen]);

  const catFilters = React.useMemo(() => ["Tous"].concat(catalogue.map((c) => c.name)), [catalogue]);
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

  // Un service n'est affiché aux utilisateurs que s'il existe au moins un
  // prestataire disponible dont les compétences le couvrent. Un pro choisit
  // un seul métier/catégorie à l'inscription (ex. "Plomberie") — sa valeur
  // `competences` est donc à comparer à la CATÉGORIE du service (s.cat),
  // jamais au titre précis du service (ex. "Réparation de fuite d'eau"),
  // sinon aucun service ne matche jamais. C'est la même correspondance que
  // le backend doit utiliser lors de la création d'une réservation.
  const servicesWithPro = React.useMemo(() => (
    services.filter((s) => allPros.some((p) => p.available && normalize(p.cat).includes(normalize(s.cat))))
  ), [services, allPros]);

  // Catalogue (page /services) filtré par catégorie ET par disponibilité
  // réelle d'un prestataire pour ce service.
  const filteredServices = React.useMemo(() => (
    catFilter === "Tous" ? servicesWithPro : servicesWithPro.filter((s) => s.cat === catFilter)
  ), [servicesWithPro, catFilter]);

  // Catégories (page /catalogue) reconstruites à partir des services
  // réservables uniquement, pour que le nombre de services affiché par
  // catégorie et les catégories elles-mêmes restent cohérents avec ce qui
  // est réellement listé ensuite sur /services.
  const catalogueWithPro = React.useMemo(() => {
    const groups = [];
    const index = new Map();
    servicesWithPro.forEach((s) => {
      const name = s.cat;
      if (!index.has(name)) {
        index.set(name, groups.length);
        groups.push({ name, items: [] });
      }
      groups[index.get(name)].items.push(s.title);
    });
    return groups.map((g, i) => ({
      name: g.name,
      slug: "cat" + i,
      color: colorForIndex(i),
      bg: "#F3F4F6",
      count: g.items.length,
      items: g.items,
      open: () => { setCatFilterState(g.name); go("services"); },
    }));
  }, [servicesWithPro]);

  const filteredCatalogue = React.useMemo(() => (
    catFilter === "Tous" ? catalogueWithPro : catalogueWithPro.filter((c) => c.name === catFilter)
  ), [catalogueWithPro, catFilter]);

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
    isPanier: screen === "panier",
    isPaiement: screen === "paiement", isConfirm: screen === "confirm",
    isDashClient: screen === "dashclient",
    isDashPro: screen === "dashpro", isAdmin: screen === "administration",
    isCatalogue: screen === "catalogue", isProLanding: screen === "prolanding",
    isApropos: screen === "apropos", isTarifs: screen === "tarifs", isBlog: screen === "blog",
    isAide: screen === "aide", isContact: screen === "contact", isFaq: screen === "faq", isLegal: screen === "legal",
    isNotFound: screen === "notfound", isFactures: screen === "factures",
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
    prosLoading, prosError,
    selectedService,
    selectedPro, selectedProStats, selectedProAvis, selectedProAvisQuery,
    reservationsClient,
    reservationsClientLoading: reservationsClientQuery.isLoading,
    reservationsClientError: reservationsClientQuery.isError,
    clientStats,
    annulerReservation,
    laisserAvisSurReservation,
    canReviewSelectedPro,
    laisserAvisSurProfil,
    laisserAvisMutation,
    laisserAvisProfilMutation,
    reservationsPro,
    reservationsProLoading: agendaProQuery.isLoading,
    reservationsProError: agendaProQuery.isError,
    reservationsProErrorDetail: agendaProQuery.error
      ? `${agendaProQuery.error?.response?.status || ""} ${agendaProQuery.error?.response?.data?.error || agendaProQuery.error?.message || ""}`.trim()
      : "",
    demandesEnAttente,
    proStats,
    proRevenueWeek,
    mesServices,
    mesAvis,
    mesAvisLoading: mesAvisQuery.isLoading,
    accepterDemande,
    refuserDemande,
    terminerPrestation,
    authUserId,
    isProRole,
    me,
    uploadPhotoMutation,
    updateProfileMutation,
    fcmTokenMutation,
    disposList,
    disposLoading: disposQuery.isLoading,
    ajouterDisponibilite,
    supprimerDisponibilite,
    updateAvailabilityMutation,
    litigesOuverts,
    litigesOuvertsLoading: litigesOuvertsQuery.isLoading,
    litigesOuvertsError: litigesOuvertsQuery.isError,
    resoudreLitige,
    rejeterLitige,
    tousLitiges,
    tousLitigesLoading: tousLitigesQuery.isLoading,
    tousLitigesError: tousLitigesQuery.isError,
    adminKpis,
    adminKpisLoading: adminKpisQuery.isLoading,
    adminRevenusMensuels,
    adminTopCategories,
    adminUsersLoading: adminUsersQuery.isLoading,
    validerPrestataire,
    suspendrePrestataire,
    statutPrestataireMutation,
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
    catFilters, catFilterList, filteredCatalogue, filteredServices, catalogue, goToServiceCategory,
    servicesLoading: servicesQuery.isLoading,
    servicesError: servicesQuery.isError,
    noServices: !servicesQuery.isLoading && !servicesQuery.isError && services.length === 0,
    convList, activeConv,
    draft, onDraft, onKey, sendMsg,
    nav,
    navAccueil: navColor("accueil"),
    navServices: (screen === "catalogue" || screen === "services") ? "#19355F" : "#4B5563",
    navPros: navColor("pros"),
    pros, featured,
    selectedServiceId, setSelectedServiceId,
    selectedProId, setSelectedProId,
    selectedReservationId, setSelectedReservationId,
    selectedReservationMontant, setSelectedReservationMontant,
    selectedReservationIds, checkoutSummary,
    panier, panierCount, panierTotal,
    ajouterAuPanier, retirerDuPanier, viderPanier, validerPanier,
    panierCheckoutPending, panierCheckoutError,
    setPanierCheckoutError,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export { AppContext, AppProvider, useApp };
