import React from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { useHashPath, screenForPath, navigateTo } from "./router.jsx";
import * as servicesApi from "./api/services.js";
import * as prestatairesApi from "./api/prestataires.js";
import * as usersApi from "./api/users.js";
import * as reservationsApi from "./api/reservations.js";
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
  const [selectedServiceId, setSelectedServiceId] = React.useState(null);
  const [selectedProId, setSelectedProId] = React.useState(null);
  const [selectedReservationId, setSelectedReservationId] = React.useState(null);
  const [selectedReservationMontant, setSelectedReservationMontant] = React.useState(0);

  const { isAuthenticated, isClient, isPro: isProRole, userId: authUserId } = useAuth();

  const meQuery = useQuery({
    queryKey: ["users", authUserId],
    queryFn: () => usersApi.getUser(authUserId),
    enabled: isAuthenticated && !!authUserId,
  });
  const me = React.useMemo(() => {
    const u = meQuery.data;
    const nom = field(u, "nom", "name") || "";
    const initials = nom.split(/\s+/).filter(Boolean).slice(0, 2).map((w) => w[0]).join("").toUpperCase();
    return {
      nom: nom || "Utilisateur",
      initials: initials || "U",
      ville: field(u, "zoneIntervention") || field(u, "adresseParDefaut") || "",
      email: field(u, "e-mail", "email") || "",
      telephone: field(u, "téléphone", "telephone") || "",
      photoUrl: authUserId ? usersApi.getUserPhotoUrl(authUserId) : "",
    };
  }, [meQuery.data, authUserId]);

  const uploadPhotoMutation = useMutation({
    mutationFn: (file) => usersApi.uploadUserPhoto(authUserId, file),
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
      Menage: () => setCatFilterAndGo("Ménage"),
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

  // GET /api/prestataires/recherche — recherche publique (l'API accepte des
  // filtres optionnels ; on lui envoie le texte et la ville recherchés, et on
  // affine ensuite côté client avec les filtres de catégorie/note/dispo qui ne
  // sont pas garantis côté serveur).
  const prestatairesQuery = useQuery({
    queryKey: ["prestataires", appliedQ, appliedCity],
    queryFn: () => prestatairesApi.rechercherPrestataires({
      q: appliedQ || undefined,
      recherche: appliedQ || undefined,
      ville: appliedCity || undefined,
      city: appliedCity || undefined,
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
      open: () => { setSelectedProId(id); go("profil"); },
    };
  }), [rawPros]);

  const pros = React.useMemo(() => allPros.slice(0, 4), [allPros]);
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
    queryKey: ["reservations", selectedProId, "avis"],
    queryFn: () => reservationsApi.getAvis(selectedProId),
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
      photoUrl: usersApi.getUserPhotoUrl(selectedProId),
    };
  }, [selectedProUserQuery.data, selectedProFromList, selectedProId]);

  const selectedProStats = selectedProStatsQuery.data || null;
  const selectedProAvis = toArray(selectedProAvisQuery.data);

  // ---- GET /api/reservations/moi/client — historique du client connecté ----
  const reservationsClientQuery = useQuery({
    queryKey: ["reservations", "moi", "client"],
    queryFn: reservationsApi.getMesReservationsClient,
    enabled: isAuthenticated && isClient,
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
    const total = list.reduce((sum, r) => sum + (Number(r.montant) || 0), 0);
    return { upcoming, done, total };
  }, [reservationsClient]);

  const updateStatutMutation = useMutation({
    mutationFn: ({ id, statut, prestataireId }) => reservationsApi.updateStatut(id, statut, prestataireId),
    onSuccess: () => reservationsClientQuery.refetch(),
  });

  const laisserAvisMutation = useMutation({
    mutationFn: ({ id, note, commentaire, clientId }) => reservationsApi.laisserAvis(id, { note, commentaire }, clientId),
    onSuccess: () => reservationsClientQuery.refetch(),
  });

  // NB: côté backend, PUT /reservations/{id}/statut est réservé aux PRESTATAIRES
  // (vérification explicite du rôle + de l'ID prestataire). Un client ne peut donc
  // pas annuler sa propre réservation via cet endpoint : l'appel échouera avec un
  // 403 "Accès réservé aux prestataires". Il n'existe pas d'endpoint d'annulation
  // côté client dans l'API fournie.
  function annulerReservation(id) {
    updateStatutMutation.mutate({ id, statut: "ANNULEE", prestataireId: authUserId });
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
  });

  const reservationsPro = React.useMemo(() => (
    toArray(agendaProQuery.data).map(normalizeReservation)
  ), [agendaProQuery.data]);

  const demandesEnAttente = React.useMemo(() => (
    reservationsPro.filter((r) => r.statut === "EN_ATTENTE")
  ), [reservationsPro]);

  const proStatsQuery = useQuery({
    queryKey: ["prestataires", authUserId, "statistiques"],
    queryFn: () => prestatairesApi.getStatistiques(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
  });

  const proRevenueWeekQuery = useQuery({
    queryKey: ["prestataires", authUserId, "revenue-week"],
    queryFn: () => prestatairesApi.getRevenueWeek(authUserId),
    enabled: isAuthenticated && isProRole && !!authUserId,
  });

  const proStats = proStatsQuery.data || {};
  const proRevenueWeek = React.useMemo(() => (
    toArray(proRevenueWeekQuery.data).map((d, i) => ({
      jour: field(d, "jour", "day") || ["L", "M", "M", "J", "V", "S", "D"][i] || "",
      montant: Number(field(d, "montant", "revenu", "value")) || 0,
    }))
  ), [proRevenueWeekQuery.data]);

  const accepterDemande = (id) => updateStatutMutation.mutate({ id, statut: "ACCEPTEE", prestataireId: authUserId }, { onSuccess: () => agendaProQuery.refetch() });
  const refuserDemande = (id) => updateStatutMutation.mutate({ id, statut: "REFUSEE", prestataireId: authUserId }, { onSuccess: () => agendaProQuery.refetch() });

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

  // ---- Admin : litiges ouverts (seul point d'administration exposé par l'API) ----
  const litigesOuvertsQuery = useQuery({
    queryKey: ["reservations", "litiges", "ouverts"],
    queryFn: reservationsApi.getLitigesOuverts,
    enabled: isAuthenticated,
  });

  const litigesOuverts = React.useMemo(() => {
    return toArray(litigesOuvertsQuery.data).map((l, i) => {
      const reservation = field(l, "réservation", "reservation") || {};
      const client = field(reservation, "client") || field(l, "client") || {};
      const prestataire = field(reservation, "prestataire") || field(l, "prestataire") || {};
      return {
        litigeId: field(l, "identifiant", "id", "litigeId"),
        reservationId: field(reservation, "identifiant", "id") || field(l, "reservationId"),
        motif: field(l, "motif") || "",
        clientNom: field(client, "nom", "name") || "Client",
        proNom: field(prestataire, "nom", "name") || "Prestataire",
        statut: field(l, "statut") || "OUVERT",
        key: field(l, "identifiant", "id", "litigeId") ?? i,
      };
    });
  }, [litigesOuvertsQuery.data]);

  const resoudreLitigeMutation = useMutation({
    mutationFn: ({ litigeId, resolution }) => reservationsApi.resoudreLitige(litigeId, resolution),
    onSuccess: () => litigesOuvertsQuery.refetch(),
  });

  function resoudreLitige(litigeId) {
    const resolution = window.prompt("Décision / résolution du litige :", "");
    if (resolution === null) return;
    resoudreLitigeMutation.mutate({ litigeId, resolution });
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

  const metiers = React.useMemo(() => catalogue.map((c) => c.name), [catalogue]);

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

  const filteredCatalogue = React.useMemo(() => (
    catFilter === "Tous" ? catalogue : catalogue.filter((c) => c.name === catFilter)
  ), [catalogue, catFilter]);

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
    prosLoading, prosError,
    selectedService,
    selectedPro, selectedProStats, selectedProAvis,
    reservationsClient,
    reservationsClientLoading: reservationsClientQuery.isLoading,
    reservationsClientError: reservationsClientQuery.isError,
    clientStats,
    annulerReservation,
    laisserAvisSurReservation,
    reservationsPro,
    reservationsProLoading: agendaProQuery.isLoading,
    reservationsProError: agendaProQuery.isError,
    demandesEnAttente,
    proStats,
    proRevenueWeek,
    accepterDemande,
    refuserDemande,
    authUserId,
    me,
    uploadPhotoMutation,
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
    catFilters, catFilterList, filteredCatalogue, catalogue,
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
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export { AppContext, AppProvider, useApp };
