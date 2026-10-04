UPDATE partner SET description = CONCAT(
    'Bruxelles Yoga Studio accueille celles et ceux qui veulent prendre soin de leur corps et de leur esprit, sans pression de performance. Nos cours en petits groupes laissent à chaque participant le temps de trouver ses repères, qu’il débute ou pratique depuis des années.',
    '\n\n',
    'Du réveil en douceur du matin au flow plus dynamique, chaque séance dure une heure et se termine par un temps de relaxation. Nos enseignants adaptent les postures à chacun : personne n’est trop raide ni trop pressé pour essayer.'
) WHERE id = 1;

UPDATE partner SET description = CONCAT(
    'Escape Horizon conçoit des jeux d’évasion où l’histoire compte autant que les énigmes. Dans chacune de nos salles, votre équipe dispose de 90 minutes pour comprendre ce qui s’est passé, fouiller le décor et trouver la sortie.',
    '\n\n',
    'Nos scénarios se jouent à partir de 12 ans, entre amis, en famille ou entre collègues. Un maître du jeu suit votre partie en direct et vous glisse un indice si vous êtes bloqués : le but est de sortir ensemble, pas de rester coincés.'
) WHERE id = 2;

UPDATE partner SET description = CONCAT(
    'Chez Cuisine & Partage, on cuisine ensemble et on mange ensemble. Nos ateliers de deux heures réunissent des participants de tous niveaux autour d’un plan de travail, d’un chef patient et d’une recette à réussir de bout en bout.',
    '\n\n',
    'Pâtes fraîches, pâtisserie belge ou saveurs du monde : chacun met la main à la pâte, pose ses questions et repart avec les gestes appris. L’atelier se termine toujours par une dégustation, parce qu’un repas se partage.'
) WHERE id = 3;

UPDATE partner SET description = CONCAT(
    'Vertical Adventure fait découvrir l’escalade à tous ceux qui n’ont jamais osé lever les yeux vers une paroi. Nos moniteurs encadrent chaque séance de 90 minutes, du premier nœud d’encordement au passage délicat qui résiste.',
    '\n\n',
    'En salle sur nos blocs et nos voies, ou en extérieur sur un site naturel, la progression se fait à votre rythme et toujours en sécurité. Les séances sont ouvertes dès 10 ans : il suffit d’une paire de chaussures de sport et d’un peu de curiosité.'
) WHERE id = 4;