ALTER TABLE partner
    ADD COLUMN slug VARCHAR(255) NULL;

UPDATE partner SET slug = 'bruxelles-yoga-studio' WHERE id = 1;
UPDATE partner SET slug = 'escape-horizon' WHERE id = 2;
UPDATE partner SET slug = 'cuisine-partage' WHERE id = 3;
UPDATE partner SET slug = 'vertical-adventure' WHERE id = 4;
UPDATE partner SET slug = 'nature-active' WHERE id = 5;
UPDATE partner SET slug = 'paintball-arena' WHERE id = 6;
UPDATE partner SET slug = 'move-together' WHERE id = 7;
UPDATE partner SET slug = 'atelier-creatif' WHERE id = 8;
UPDATE partner SET slug = 'cyclo-decouverte' WHERE id = 9;
UPDATE partner SET slug = 'aqua-fun' WHERE id = 10;
UPDATE partner SET slug = 'zen-forme' WHERE id = 11;
UPDATE partner SET slug = 'adventure-park' WHERE id = 12;
UPDATE partner SET slug = 'danse-avenue' WHERE id = 13;
UPDATE partner SET slug = 'paddle-club' WHERE id = 14;
UPDATE partner SET slug = 'kids-factory' WHERE id = 15;
UPDATE partner SET slug = 'run-brussels' WHERE id = 16;
UPDATE partner SET slug = 'photo-walk' WHERE id = 17;
UPDATE partner SET slug = 'horse-nature' WHERE id = 18;
UPDATE partner SET slug = 'racket-center' WHERE id = 19;
UPDATE partner SET slug = 'urban-climb' WHERE id = 20;
UPDATE partner SET slug = 'mindful-moments' WHERE id = 21;
UPDATE partner SET slug = 'kayak-valley' WHERE id = 22;
UPDATE partner SET slug = 'board-games-house' WHERE id = 23;
UPDATE partner SET slug = 'fitness-lab' WHERE id = 24;
UPDATE partner SET slug = 'ceramic-corner' WHERE id = 25;
UPDATE partner SET slug = 'trail-explorer' WHERE id = 26;
UPDATE partner SET slug = 'music-workshop' WHERE id = 27;
UPDATE partner SET slug = 'skate-academy' WHERE id = 28;
UPDATE partner SET slug = 'green-garden' WHERE id = 29;
UPDATE partner SET slug = 'archery-club' WHERE id = 30;
UPDATE partner SET slug = 'chocolate-experience' WHERE id = 31;
UPDATE partner SET slug = 'sailing-point' WHERE id = 32;
UPDATE partner SET slug = 'theatre-studio' WHERE id = 33;
UPDATE partner SET slug = 'boxing-spirit' WHERE id = 34;
UPDATE partner SET slug = 'robot-kids' WHERE id = 35;
UPDATE partner SET slug = 'golf-initiation' WHERE id = 36;
UPDATE partner SET slug = 'relax-spa' WHERE id = 37;
UPDATE partner SET slug = 'city-discovery' WHERE id = 38;
UPDATE partner SET slug = 'cooking-junior' WHERE id = 39;
UPDATE partner SET slug = 'outdoor-challenge' WHERE id = 40;

UPDATE partner SET slug = CONCAT('partenaire-', id) WHERE slug IS NULL;

ALTER TABLE partner
    MODIFY COLUMN slug VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_partner_slug UNIQUE (slug);