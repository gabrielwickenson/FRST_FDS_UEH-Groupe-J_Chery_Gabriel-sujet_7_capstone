const DEFAULT_WIDTH = 800;
const DEFAULT_HEIGHT = 600;

export function img(key, width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT) {
  const safeKey = encodeURIComponent(String(key || "kolabor"));
  const safeWidth = Number.isFinite(width) ? Math.max(1, Math.floor(width)) : DEFAULT_WIDTH;
  const safeHeight = Number.isFinite(height) ? Math.max(1, Math.floor(height)) : DEFAULT_HEIGHT;

  return `https://picsum.photos/seed/${safeKey}/${safeWidth}/${safeHeight}`;
}

// ---- Photos adaptées au contenu ----
// LoremFlickr sert des photos réelles filtrées par mots-clés ; `lock` fige
// l'image pour un service donné (stable entre deux rendus). On mappe les
// catégories/titres français vers des mots-clés anglais qui donnent de bons
// résultats photo.
// Photos RÉELLES de professionnels au travail, choisies une par une sur
// Unsplash pour correspondre exactement au métier (URL fixes — jamais
// aléatoires). Chaque catégorie pointe vers une photo précise et vérifiée.
const PHOTO_MAP = [
  { match: ["menage", "nettoyage", "repassage", "vitres", "lessive", "linge", "blanchisserie"],
    photos: ["1581578731548-c64695cc6952", "1584820927498-cfe5211fd8bf", "1563453392212-326f5e854473", "1528740561666-dc2479dc08ab"] },
  { match: ["plomb", "fuite", "canalisation", "sanitaire", "robinet"],
    photos: ["1585704032915-c3400ca199e7", "1581244277943-fe4a9c777189"] },
  { match: ["electric", "cablage", "disjoncteur", "lampe"],
    photos: ["1621905252507-b35492cc74b4", "1565608438257-fac3c27beb36"] },
  { match: ["climatisation", "refrigerat", "froid", "ventilation"],
    photos: ["1581094794329-c8112a89af12", "1621905251189-08b45d6a269e"] },
  { match: ["jardin", "pelouse", "haie", "arbre", "espace vert"],
    photos: ["1416879595882-3373a0480b5b", "1466692476868-aef1dfb1e735", "1523348837708-15d4a09cfac2"] },
  { match: ["peinture", "peintre", "mur"],
    photos: ["1589939705384-5185137a7f0f", "1562259949-e8e7689d7828"] },
  { match: ["menuiserie", "bois", "meuble", "porte"],
    photos: ["1504148455328-c376907d081c", "1416339306562-f3d12fefd36f"] },
  { match: ["maconnerie", "construction", "renovation", "travaux", "toiture", "toit", "charpente", "securite", "gardien", "surveillance", "alarme"],
    photos: ["1504307651254-35680f356dfd", "1541888946425-d81bb19240f5", "1503387762-592deb58ef4e"] },
  { match: ["informatique", "ordinateur", "reseau", "depannage pc"],
    photos: ["1486312338219-ce68d2c6f44d", "1517430816045-df4b7de11d1d", "1498050108023-c5249f4df085"] },
  { match: ["cuisine", "cuisinier", "repas", "traiteur"],
    photos: ["1556911220-bff31c812dba", "1577219491135-ce391730fb2c", "1466637574441-749b8f19452f"] },
  { match: ["coiffure", "coiffeur", "barbier"],
    photos: ["1503951914875-452162b0f3f1", "1585747860715-2ba37e788b70"] },
  { match: ["beaute", "esthetique", "manucure", "massage"],
    photos: ["1544161515-4ab6ce6db874", "1540555700478-4be289fbecef"] },
  { match: ["mecanique", "voiture", "auto", "moto"],
    photos: ["1487754180451-c456f719a1fc", "1486262715619-67b85e0b08d3"] },
];
const DEFAULT_PHOTOS = ["1521737604893-d14cc237f11d", "1600880292203-757bb62b4baf", "1522071820081-009f0129c71c"];

function normalizeKw(x) {
  return (x || "").toString().toLowerCase().normalize("NFD").replace(/[̀-ͯ]/g, "");
}

export function serviceImg(title, cat, id, width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT) {
  const text = `${normalizeKw(title)} ${normalizeKw(cat)}`;
  const entry = PHOTO_MAP.find((e) => e.match.some((m) => text.includes(m)));
  const photos = entry ? entry.photos : DEFAULT_PHOTOS;
  // Sélection stable par service : deux services différents de la même
  // catégorie reçoivent des photos différentes (pas de doublons tant que la
  // catégorie a assez de photos), et un même service garde toujours la sienne.
  const n = Number.isFinite(Number(id))
    ? Number(id)
    : String(id ?? "").split("").reduce((s, c) => s + c.charCodeAt(0), 0);
  const photo = photos[Math.abs(n) % photos.length];
  const w = Number.isFinite(width) ? Math.max(1, Math.floor(width)) : DEFAULT_WIDTH;
  const h = Number.isFinite(height) ? Math.max(1, Math.floor(height)) : DEFAULT_HEIGHT;
  return `https://images.unsplash.com/photo-${photo}?auto=format&fit=crop&w=${w}&h=${h}&q=70`;
}

export { img as default };
