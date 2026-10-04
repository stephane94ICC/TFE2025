UPDATE activity SET description = 'Une heure pour bien commencer la journée : réveil articulaire, salutations au soleil et postures simples, enchaînées au rythme de la respiration. La séance convient aux débutants et se termine par quelques minutes de relaxation.'
WHERE partner_id = 1 AND title = 'Yoga du matin';

UPDATE activity SET description = 'Une pratique lente, centrée sur le souffle et le relâchement des tensions. Les postures sont tenues longtemps, avec des variantes pour chaque niveau de souplesse. Idéal après une semaine chargée, ou pour reprendre une activité physique en douceur.'
WHERE partner_id = 1 AND title = 'Yoga doux et respiration';

UPDATE activity SET description = 'Un enchaînement fluide de postures guidé par la respiration, plus dynamique qu’un cours classique. On y travaille la force, l’équilibre et la concentration. Quelques notions de yoga sont un plus, mais l’enseignant propose des alternatives à chaque étape.'
WHERE partner_id = 1 AND title = 'Vinyasa flow';

UPDATE activity SET description = 'Le dernier train de nuit s’est arrêté en rase campagne et le contrôleur a disparu. Fouillez les compartiments, décodez les messages laissés à bord et relancez le convoi avant l’aube. Un scénario idéal pour une première expérience d’escape game.'
WHERE partner_id = 2 AND title = 'Escape game — Le dernier train';

UPDATE activity SET description = 'Une expérience a mal tourné et le laboratoire s’est verrouillé automatiquement. Analysez les échantillons, reconstituez les notes du chercheur et levez le confinement avant la fin du compte à rebours. Des énigmes logiques pour les équipes qui aiment chercher.'
WHERE partner_id = 2 AND title = 'Escape game — Le laboratoire';

UPDATE activity SET description = 'Sur une île battue par les vents, le phare est éteint depuis des années. Explorez la tour, déchiffrez le journal du gardien et rallumez la lumière pour guider les bateaux. Notre scénario le plus immersif, pour les équipes déjà initiées.'
WHERE partner_id = 2 AND title = 'Escape game — Le phare oublié';

UPDATE activity SET description = 'Farine, œufs et un peu de patience : apprenez à pétrir, abaisser et découper vos propres pâtes, des tagliatelles aux raviolis farcis. Le chef partage ses astuces pour réussir la pâte à chaque fois, et l’atelier se conclut par une dégustation de vos créations.'
WHERE partner_id = 3 AND title = 'Atelier pâtes fraîches';

UPDATE activity SET description = 'Un voyage culinaire en deux heures : chaque atelier explore une cuisine différente, de l’Asie à l’Amérique latine. Épices, techniques et produits sont expliqués pas à pas, pour refaire facilement les recettes chez soi. Dégustation commune en fin de séance.'
WHERE partner_id = 3 AND title = 'Atelier cuisine du monde';

UPDATE activity SET description = 'Gaufres, spéculoos ou tarte au sucre : découvrez les secrets des douceurs belges et repartez avec les bons gestes. Pesées, cuisson et dressage sont réalisés par les participants eux-mêmes, accompagnés d’un pâtissier. Dégustation à partager en fin d’atelier.'
WHERE partner_id = 3 AND title = 'Atelier pâtisserie belge';

UPDATE activity SET description = 'Votre première séance d’escalade, en toute sécurité. Le moniteur vous apprend à vous équiper, à faire le nœud d’encordement et à assurer un partenaire, puis vous grimpez vos premières voies. Aucune expérience n’est nécessaire.'
WHERE partner_id = 4 AND title = 'Initiation à l’escalade en salle';

UPDATE activity SET description = 'Le bloc se pratique sans corde, sur des murs de faible hauteur protégés par d’épais tapis. On y travaille la technique et la lecture des passages, sous forme de petits défis à résoudre. Parfait pour progresser vite, seul ou entre amis.'
WHERE partner_id = 4 AND title = 'Escalade de bloc';

UPDATE activity SET description = 'Quittez la salle pour un vrai rocher : un moniteur vous accompagne sur un site naturel adapté à votre niveau. Lecture du terrain, gestion de l’effort et règles de sécurité en extérieur sont au programme. Une première expérience en salle est recommandée.'
WHERE partner_id = 4 AND title = 'Voie extérieure encadrée';