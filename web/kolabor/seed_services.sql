-- =====================================================================
-- Kolabor - Peuplement de la table `service`
-- 24 catégories x 5 sous-services = 120 lignes
-- À coller dans phpMyAdmin (onglet SQL) sur la base service_platform_db
-- La colonne `id` est en AUTO_INCREMENT (IDENTITY côté Hibernate),
-- on ne la précise donc pas ici.
-- =====================================================================

INSERT INTO service (categorie, nom, description) VALUES
-- 1. Ménage / Entretien de la maison
('Ménage', 'Nettoyage complet de la maison', 'Nettoyage en profondeur de toutes les pièces, sols, surfaces et sanitaires.'),
('Ménage', 'Nettoyage de bureau', 'Entretien régulier des espaces de travail et bureaux professionnels.'),
('Ménage', 'Nettoyage après travaux', 'Élimination de la poussière et des résidus après rénovation ou construction.'),
('Ménage', 'Nettoyage de vitres', 'Lavage et lustrage des vitres, fenêtres et baies vitrées.'),
('Ménage', 'Nettoyage de tapis et moquettes', 'Shampouinage et détachage des tapis, moquettes et rideaux.'),

-- 2. Lessive & Repassage
('Lessive & Repassage', 'Lessive à domicile', 'Lavage du linge directement chez le client.'),
('Lessive & Repassage', 'Repassage de vêtements', 'Repassage soigné de tous types de vêtements.'),
('Lessive & Repassage', 'Pressing et nettoyage à sec', 'Nettoyage à sec pour vêtements délicats et costumes.'),
('Lessive & Repassage', 'Blanchisserie hebdomadaire', 'Formule d''entretien régulier du linge de maison.'),
('Lessive & Repassage', 'Nettoyage de rideaux', 'Lavage et repassage des rideaux et voilages.'),

-- 3. Plomberie
('Plomberie', 'Réparation de fuite d''eau', 'Détection et réparation rapide des fuites d''eau.'),
('Plomberie', 'Débouchage de canalisation', 'Débouchage d''éviers, toilettes et canalisations obstruées.'),
('Plomberie', 'Installation sanitaire', 'Pose de lavabos, éviers, douches et toilettes.'),
('Plomberie', 'Installation de chauffe-eau', 'Installation et raccordement de chauffe-eau.'),
('Plomberie', 'Dépannage urgence plomberie', 'Intervention rapide pour urgence plomberie 24/7.'),

-- 4. Électricité
('Électricité', 'Installation électrique', 'Câblage et installation électrique complète.'),
('Électricité', 'Dépannage électrique urgent', 'Intervention rapide en cas de panne électrique.'),
('Électricité', 'Installation de prises et interrupteurs', 'Pose et remplacement de prises, interrupteurs et disjoncteurs.'),
('Électricité', 'Installation de luminaires', 'Pose de lustres, spots et appliques murales.'),
('Électricité', 'Mise aux normes électriques', 'Vérification et mise en conformité de l''installation électrique.'),

-- 5. Peinture
('Peinture', 'Peinture intérieure', 'Peinture des murs et plafonds intérieurs.'),
('Peinture', 'Peinture extérieure', 'Peinture de façades et surfaces extérieures.'),
('Peinture', 'Peinture de façade', 'Rénovation et embellissement de façade.'),
('Peinture', 'Décoration murale', 'Papier peint, fresques et finitions décoratives.'),
('Peinture', 'Rénovation de peinture', 'Rafraîchissement complet d''une peinture ancienne.'),

-- 6. Jardinage
('Jardinage', 'Entretien de jardin', 'Entretien régulier des espaces verts.'),
('Jardinage', 'Tonte de pelouse', 'Tonte et entretien de la pelouse.'),
('Jardinage', 'Taille de haies et arbres', 'Taille et élagage des haies et arbres.'),
('Jardinage', 'Aménagement paysager', 'Conception et aménagement d''espaces extérieurs.'),
('Jardinage', 'Arrosage automatique', 'Installation de systèmes d''arrosage automatique.'),

-- 7. Menuiserie
('Menuiserie', 'Fabrication de meubles sur mesure', 'Conception et fabrication de meubles personnalisés.'),
('Menuiserie', 'Réparation de portes et fenêtres', 'Réparation et ajustement de portes et fenêtres en bois.'),
('Menuiserie', 'Installation de placards', 'Conception et pose de placards sur mesure.'),
('Menuiserie', 'Pose de parquet', 'Installation et rénovation de parquet en bois.'),
('Menuiserie', 'Menuiserie extérieure', 'Fabrication et pose de structures en bois extérieures.'),

-- 8. Maçonnerie
('Maçonnerie', 'Construction de murs', 'Construction de murs et cloisons.'),
('Maçonnerie', 'Réparation de fissures', 'Réparation de fissures sur murs et façades.'),
('Maçonnerie', 'Carrelage et dallage', 'Pose de carrelage intérieur et extérieur.'),
('Maçonnerie', 'Rénovation de façade', 'Rénovation complète de façade en maçonnerie.'),
('Maçonnerie', 'Coulage de béton', 'Coulage de dalles et fondations en béton.'),

-- 9. Climatisation
('Climatisation', 'Installation de climatiseur', 'Installation de climatiseurs split et muraux.'),
('Climatisation', 'Entretien et nettoyage climatiseur', 'Nettoyage des filtres et entretien préventif.'),
('Climatisation', 'Réparation de climatiseur', 'Diagnostic et réparation de panne de climatisation.'),
('Climatisation', 'Recharge de gaz réfrigérant', 'Recharge du gaz réfrigérant du climatiseur.'),
('Climatisation', 'Installation de ventilateurs', 'Pose de ventilateurs de plafond et muraux.'),

-- 10. Débarras & Déchets
('Débarras & Déchets', 'Débarras de maison', 'Débarras complet de maison ou appartement.'),
('Débarras & Déchets', 'Enlèvement de gravats', 'Enlèvement de gravats après travaux.'),
('Débarras & Déchets', 'Collecte de déchets', 'Collecte régulière des déchets ménagers.'),
('Débarras & Déchets', 'Nettoyage de terrain', 'Nettoyage et débroussaillage de terrain.'),
('Débarras & Déchets', 'Recyclage et tri sélectif', 'Tri et recyclage des déchets valorisables.'),

-- 11. Déménagement
('Déménagement', 'Déménagement résidentiel', 'Déménagement complet de logement.'),
('Déménagement', 'Déménagement de bureau', 'Déménagement d''entreprises et de bureaux.'),
('Déménagement', 'Emballage et déballage', 'Service d''emballage soigné de vos biens.'),
('Déménagement', 'Transport de meubles', 'Transport sécurisé de meubles volumineux.'),
('Déménagement', 'Garde-meuble temporaire', 'Stockage temporaire de meubles et effets.'),

-- 12. Livraison
('Livraison', 'Livraison de colis', 'Livraison rapide de colis en ville.'),
('Livraison', 'Livraison de courses', 'Livraison de courses alimentaires à domicile.'),
('Livraison', 'Livraison de repas', 'Livraison de repas depuis vos restaurants préférés.'),
('Livraison', 'Coursier express', 'Service de coursier pour livraisons urgentes.'),
('Livraison', 'Livraison de marchandises', 'Transport de marchandises pour commerçants.'),

-- 13. Informatique
('Informatique', 'Réparation d''ordinateur', 'Diagnostic et réparation d''ordinateurs.'),
('Informatique', 'Installation de réseau Wi-Fi', 'Installation et configuration de réseau Wi-Fi.'),
('Informatique', 'Dépannage informatique à domicile', 'Assistance informatique directement chez vous.'),
('Informatique', 'Récupération de données', 'Récupération de fichiers et données perdues.'),
('Informatique', 'Installation de logiciels', 'Installation et configuration de logiciels.'),

-- 14. Désinfection
('Désinfection', 'Désinfection de maison', 'Désinfection complète des surfaces de la maison.'),
('Désinfection', 'Traitement anti-nuisibles', 'Traitement contre insectes et nuisibles.'),
('Désinfection', 'Dératisation', 'Élimination des rongeurs et rats.'),
('Désinfection', 'Désinsectisation', 'Traitement contre cafards, fourmis et moustiques.'),
('Désinfection', 'Désinfection de locaux commerciaux', 'Désinfection de bureaux et commerces.'),

-- 15. Sécurité
('Sécurité', 'Installation de caméras de surveillance', 'Pose et configuration de caméras de sécurité.'),
('Sécurité', 'Installation d''alarme', 'Installation de systèmes d''alarme anti-intrusion.'),
('Sécurité', 'Agent de sécurité à domicile', 'Mise à disposition d''un agent de sécurité.'),
('Sécurité', 'Contrôle d''accès', 'Installation de systèmes de contrôle d''accès.'),
('Sécurité', 'Sécurisation de portes et fenêtres', 'Renforcement de la sécurité des ouvertures.'),

-- 16. Piscine
('Piscine', 'Nettoyage de piscine', 'Nettoyage régulier du bassin et des abords.'),
('Piscine', 'Entretien chimique de piscine', 'Équilibrage du pH et traitement de l''eau.'),
('Piscine', 'Réparation de pompe à piscine', 'Diagnostic et réparation de pompe de filtration.'),
('Piscine', 'Construction de piscine', 'Construction de piscines sur mesure.'),
('Piscine', 'Ouverture et fermeture saisonnière', 'Mise en service et hivernage de la piscine.'),

-- 17. Automobile
('Automobile', 'Lavage auto à domicile', 'Lavage complet du véhicule chez vous.'),
('Automobile', 'Mécanique générale', 'Entretien et réparation mécanique courante.'),
('Automobile', 'Vidange et entretien', 'Vidange moteur et contrôle des niveaux.'),
('Automobile', 'Réparation de pneus', 'Réparation et remplacement de pneus.'),
('Automobile', 'Diagnostic électronique auto', 'Diagnostic électronique des pannes du véhicule.'),

-- 18. Famille
('Famille', 'Garde d''enfants', 'Garde d''enfants à domicile en toute confiance.'),
('Famille', 'Aide aux devoirs', 'Accompagnement scolaire et aide aux devoirs.'),
('Famille', 'Assistance aux personnes âgées', 'Aide et compagnie pour personnes âgées.'),
('Famille', 'Nounou à domicile', 'Garde régulière de jeunes enfants.'),
('Famille', 'Accompagnement familial', 'Accompagnement pour rendez-vous et déplacements.'),

-- 19. Animaux
('Animaux', 'Toilettage à domicile', 'Toilettage complet de vos animaux de compagnie.'),
('Animaux', 'Promenade de chiens', 'Sortie et promenade quotidienne de chiens.'),
('Animaux', 'Garde d''animaux', 'Garde d''animaux pendant vos absences.'),
('Animaux', 'Dressage de chiens', 'Éducation et dressage comportemental.'),
('Animaux', 'Visite vétérinaire à domicile', 'Consultation vétérinaire directement chez vous.'),

-- 20. Énergie solaire
('Énergie solaire', 'Installation de panneaux solaires', 'Installation de panneaux photovoltaïques.'),
('Énergie solaire', 'Entretien de systèmes solaires', 'Entretien et nettoyage des panneaux solaires.'),
('Énergie solaire', 'Installation d''onduleurs', 'Installation d''onduleurs et régulateurs solaires.'),
('Énergie solaire', 'Installation de batteries solaires', 'Pose de batteries de stockage solaire.'),
('Énergie solaire', 'Audit énergétique', 'Étude des besoins et rendement énergétique.'),

-- 21. Serrurerie
('Serrurerie', 'Ouverture de porte', 'Ouverture de porte claquée ou verrouillée.'),
('Serrurerie', 'Changement de serrure', 'Remplacement de serrures et cylindres.'),
('Serrurerie', 'Installation de verrou', 'Pose de verrous et systèmes de fermeture.'),
('Serrurerie', 'Duplication de clés', 'Reproduction de clés en tout genre.'),
('Serrurerie', 'Dépannage serrurerie urgence', 'Intervention rapide en cas d''urgence serrurerie.'),

-- 22. Événementiel
('Événementiel', 'Organisation de mariage', 'Organisation complète de cérémonies de mariage.'),
('Événementiel', 'Décoration d''événement', 'Décoration sur mesure pour vos événements.'),
('Événementiel', 'Location de matériel événementiel', 'Location de chaises, tables et tentes.'),
('Événementiel', 'Traiteur pour événements', 'Service traiteur pour réceptions et fêtes.'),
('Événementiel', 'Animation d''événement', 'Animation musicale et maître de cérémonie.'),

-- 23. Beauté & Bien-être
('Beauté & Bien-être', 'Coiffure à domicile', 'Prestations de coiffure directement chez vous.'),
('Beauté & Bien-être', 'Manucure et pédicure', 'Soins des mains et des pieds à domicile.'),
('Beauté & Bien-être', 'Massage à domicile', 'Séances de massage relaxant ou thérapeutique.'),
('Beauté & Bien-être', 'Maquillage professionnel', 'Maquillage pour événements et occasions spéciales.'),
('Beauté & Bien-être', 'Soins esthétiques', 'Soins du visage et du corps à domicile.'),

-- 24. Réparation électroménager
('Réparation électroménager', 'Réparation de réfrigérateur', 'Diagnostic et réparation de réfrigérateurs.'),
('Réparation électroménager', 'Réparation de machine à laver', 'Réparation de machines à laver en panne.'),
('Réparation électroménager', 'Réparation de four', 'Réparation de fours et cuisinières.'),
('Réparation électroménager', 'Réparation de micro-ondes', 'Diagnostic et réparation de micro-ondes.'),
('Réparation électroménager', 'Installation d''électroménager', 'Installation et raccordement d''appareils électroménagers.');
