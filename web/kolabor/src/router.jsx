import React from "react";

const KNOWN_SCREENS = new Set([
  "accueil",
  "services",
  "pros",
  "profil",
  "service",
  "comment",
  "login",
  "signup",
  "reserver",
  "paiement",
  "confirm",
  "dashclient",
  "messages",
  "dashpro",
  "admin",
  "catalogue",
  "prolanding",
  "apropos",
  "tarifs",
  "blog",
  "aide",
  "contact",
  "faq",
  "legal",
  "notfound",
  "favoris",
  "factures",
  "params",
  "messervices",
  "dispos",
  "revenus",
]);

function parseHash(hashValue) {
  const clean = String(hashValue || "")
    .replace(/^#\/?/, "")
    .replace(/^\/+|\/+$/g, "")
    .trim()
    .toLowerCase();

  return clean || "accueil";
}

export function screenForPath(path) {
  return KNOWN_SCREENS.has(path) ? path : "notfound";
}

export function navigateTo(screen) {
  const target = KNOWN_SCREENS.has(screen) ? screen : "notfound";
  const nextHash = `#/${target}`;

  if (window.location.hash !== nextHash) {
    window.location.hash = nextHash;
  }
}

export function useHashPath() {
  const [path, setPath] = React.useState(() => parseHash(window.location.hash));

  React.useEffect(() => {
    const onHashChange = () => {
      setPath(parseHash(window.location.hash));
    };

    window.addEventListener("hashchange", onHashChange);

    if (!window.location.hash) {
      navigateTo("accueil");
    }

    return () => {
      window.removeEventListener("hashchange", onHashChange);
    };
  }, []);

  return path;
}
