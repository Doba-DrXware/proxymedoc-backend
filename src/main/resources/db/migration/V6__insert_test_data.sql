-- Insertion des données de test
-- Les champs pour fichiers et images sont ignorés comme demandé

-- Insertion des pharmacies fictives (Dakar, Sénégal)
INSERT INTO pharmacie (nom, adresse, latitude, longitude, telephone, statut, horaires, est_de_garde, numero_licence, commentaire_pharmacie, score_ia, contact)
VALUES
    ('Pharmacie Centrale Dakar', '42 Rue Félix Faure, Dakar', 14.6928, -17.0554, '+221771234567', 'VALIDEE', 'Lun-Sam: 08h-20h, Dim: 09h-18h', false, 'PCD-2024-001', 'Pharmacie principale du centre-ville', 85, 'contact@pharmacie-centrale.sn'),
    ('Pharmacie Ngor', '12 Avenue Cheikh Anta Diop, Ngor, Dakar', 14.7497, -17.1507, '+221772234567', 'VALIDEE', 'Lun-Dim: 08h-22h', true, 'PCD-2024-002', 'Pharmacie de quartier bien achalandée', 78, 'info@pharmacie-ngor.sn'),
    ('Pharmacie Point E', '5 Boulevard de la République, Point E, Dakar', 14.6595, -17.0733, '+221773234567', 'VALIDEE', 'Lun-Sam: 07h-21h, Dim: 08h-20h', false, 'PCD-2024-003', 'Petite pharmacie quartier', 72, 'contact@pharmacie-pointe.sn'),
    ('Pharmacie Plateau', '88 Rue 1, Plateau, Dakar', 14.6678, -17.0357, '+221774234567', 'VALIDEE', 'Lun-Dim: 08h-20h', true, 'PCD-2024-004', 'Pharmacie bien équipée', 80, 'service@pharmacie-plateau.sn'),
    ('Pharmacie Grand Yoff', '15 Avenue Lamine Guèye, Grand Yoff, Dakar', 14.7200, -17.1400, '+221775234567', 'VALIDEE', 'Lun-Sam: 08h-19h, Dim: 10h-17h', false, 'PCD-2024-005', 'Pharmacie de zone d''expansion', 65, 'info@pharmacie-yoff.sn');

-- Insertion des médicaments fictifs
INSERT INTO medicament (denomination, categorie, description, prix_unitaire, forme_galenique, dosage, exige_ordonnance)
VALUES
    ('Paracétamol', 'Analgésique', 'Antipyrétique et analgésique léger', 500.0, 'Comprimé', '500mg', false),
    ('Ibuprofène', 'Analgésique', 'Anti-inflammatoire non stéroïdien', 1200.0, 'Comprimé', '400mg', false),
    ('Amoxicilline', 'Antibiotique', 'Antibiotique bêta-lactamine', 2500.0, 'Comprimé', '500mg', true),
    ('Ciprofloxacine', 'Antibiotique', 'Fluoroquinolone à large spectre', 3000.0, 'Comprimé', '500mg', true),
    ('Métformine', 'Antidiabétique', 'Médicament antidiabétique oral', 4000.0, 'Comprimé', '500mg', true),
    ('Atenolol', 'Antihypertenseur', 'Bêta-bloquant cardiosélectif', 3500.0, 'Comprimé', '50mg', true),
    ('Aspirine', 'Analgésique', 'Acide acétylsalicylique pour douleurs légères', 800.0, 'Comprimé', '300mg', false),
    ('Oméprazole', 'Gastriques', 'Inhibiteur de pompe à protons', 5500.0, 'Gélule', '20mg', true),
    ('Loratadine', 'Antihistaminique', 'Antihistaminique H1 non sédatif', 2000.0, 'Comprimé', '10mg', false),
    ('Cétirizine', 'Antihistaminique', 'Antihistaminique pour allergies', 2200.0, 'Comprimé', '10mg', false),
    ('Vitamine C', 'Vitamine', 'Acide ascorbique pour immunité', 1500.0, 'Comprimé', NULL, false),
    ('Magnésium', 'Minéral', 'Complément magnésium', 2500.0, 'Comprimé', NULL, false),
    ('Fluconazole', 'Antifongique', 'Antifongique azolé', 6000.0, 'Gélule', '150mg', true),
    ('Ceftriaxone', 'Antibiotique', 'Céphalosporine de 3ème génération', 7500.0, 'Poudre pour injection', '1g', true),
    ('Acétaminophène', 'Analgésique', 'Analgésique et antipyrétique', 600.0, 'Sirop', NULL, false),
    ('Chloroquine', 'Antipaludique', 'Traitement antipaludique', 3200.0, 'Comprimé', '250mg', true),
    ('Artémether', 'Antipaludique', 'Dérivé d''artémisinine pour paludisme sévère', 8000.0, 'Injection', '80mg/1ml', true),
    ('Clotrimazole', 'Antifongique', 'Antifongique topique', 1800.0, 'Crème', NULL, false),
    ('Doxycycline', 'Antibiotique', 'Tétracycline pour infections', 2800.0, 'Comprimé', '100mg', true),
    ('Metronidazole', 'Antiparasitaire', 'Antiparasitaire et antibactérien', 1600.0, 'Comprimé', '250mg', true);

-- Insertion des données de stock
-- Pour chaque médicament (20), on crée des stocks dans les 5 pharmacies (100 lignes au total)
-- Avec des quantités variées pour simuler différentes situations

-- Medicament 1: Paracétamol (id=1)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (150, 50, CURRENT_DATE, 1, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (200, 50, CURRENT_DATE, 1, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (80, 50, CURRENT_DATE, 1, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (300, 100, CURRENT_DATE, 1, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (45, 50, CURRENT_DATE, 1, 5);

-- Medicament 2: Ibuprofène (id=2)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (120, 40, CURRENT_DATE, 2, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (0, 40, CURRENT_DATE, 2, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (95, 40, CURRENT_DATE, 2, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (250, 80, CURRENT_DATE, 2, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (110, 40, CURRENT_DATE, 2, 5);

-- Medicament 3: Amoxicilline (id=3)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (80, 30, CURRENT_DATE, 3, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (120, 30, CURRENT_DATE, 3, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (35, 30, CURRENT_DATE, 3, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (200, 60, CURRENT_DATE, 3, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (75, 30, CURRENT_DATE, 3, 5);

-- Medicament 4: Ciprofloxacine (id=4)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (60, 20, CURRENT_DATE, 4, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (90, 20, CURRENT_DATE, 4, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (25, 20, CURRENT_DATE, 4, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (150, 50, CURRENT_DATE, 4, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (55, 20, CURRENT_DATE, 4, 5);

-- Medicament 5: Métformine (id=5)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (140, 50, CURRENT_DATE, 5, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (180, 50, CURRENT_DATE, 5, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (75, 50, CURRENT_DATE, 5, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (280, 100, CURRENT_DATE, 5, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (100, 50, CURRENT_DATE, 5, 5);

-- Medicament 6: Atenolol (id=6)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (70, 25, CURRENT_DATE, 6, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (110, 25, CURRENT_DATE, 6, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (40, 25, CURRENT_DATE, 6, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (170, 60, CURRENT_DATE, 6, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (65, 25, CURRENT_DATE, 6, 5);

-- Medicament 7: Aspirine (id=7)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (200, 60, CURRENT_DATE, 7, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (250, 60, CURRENT_DATE, 7, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (120, 60, CURRENT_DATE, 7, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (350, 120, CURRENT_DATE, 7, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (180, 60, CURRENT_DATE, 7, 5);

-- Medicament 8: Oméprazole (id=8)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (90, 35, CURRENT_DATE, 8, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (140, 35, CURRENT_DATE, 8, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (15, 35, CURRENT_DATE, 8, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (220, 80, CURRENT_DATE, 8, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (85, 35, CURRENT_DATE, 8, 5);

-- Medicament 9: Loratadine (id=9)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (110, 40, CURRENT_DATE, 9, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (160, 40, CURRENT_DATE, 9, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (50, 40, CURRENT_DATE, 9, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (260, 90, CURRENT_DATE, 9, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (95, 40, CURRENT_DATE, 9, 5);

-- Medicament 10: Cétirizine (id=10)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (100, 38, CURRENT_DATE, 10, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (150, 38, CURRENT_DATE, 10, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (55, 38, CURRENT_DATE, 10, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (240, 85, CURRENT_DATE, 10, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (90, 38, CURRENT_DATE, 10, 5);

-- Medicament 11: Vitamine C (id=11)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (180, 60, CURRENT_DATE, 11, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (220, 60, CURRENT_DATE, 11, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (100, 60, CURRENT_DATE, 11, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (300, 100, CURRENT_DATE, 11, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (140, 60, CURRENT_DATE, 11, 5);

-- Medicament 12: Magnésium (id=12)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (85, 30, CURRENT_DATE, 12, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (130, 30, CURRENT_DATE, 12, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (60, 30, CURRENT_DATE, 12, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (190, 70, CURRENT_DATE, 12, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (75, 30, CURRENT_DATE, 12, 5);

-- Medicament 13: Fluconazole (id=13)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (30, 15, CURRENT_DATE, 13, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (50, 15, CURRENT_DATE, 13, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (0, 15, CURRENT_DATE, 13, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (80, 40, CURRENT_DATE, 13, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (25, 15, CURRENT_DATE, 13, 5);

-- Medicament 14: Ceftriaxone (id=14)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (40, 20, CURRENT_DATE, 14, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (60, 20, CURRENT_DATE, 14, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (15, 20, CURRENT_DATE, 14, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (100, 50, CURRENT_DATE, 14, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (35, 20, CURRENT_DATE, 14, 5);

-- Medicament 15: Acétaminophène (id=15)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (120, 45, CURRENT_DATE, 15, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (170, 45, CURRENT_DATE, 15, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (65, 45, CURRENT_DATE, 15, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (240, 90, CURRENT_DATE, 15, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (105, 45, CURRENT_DATE, 15, 5);

-- Medicament 16: Chloroquine (id=16)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (70, 28, CURRENT_DATE, 16, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (105, 28, CURRENT_DATE, 16, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (40, 28, CURRENT_DATE, 16, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (160, 70, CURRENT_DATE, 16, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (60, 28, CURRENT_DATE, 16, 5);

-- Medicament 17: Artémether (id=17)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (25, 10, CURRENT_DATE, 17, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (35, 10, CURRENT_DATE, 17, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (5, 10, CURRENT_DATE, 17, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (55, 30, CURRENT_DATE, 17, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (20, 10, CURRENT_DATE, 17, 5);

-- Medicament 18: Clotrimazole (id=18)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (95, 35, CURRENT_DATE, 18, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (140, 35, CURRENT_DATE, 18, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (50, 35, CURRENT_DATE, 18, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (210, 80, CURRENT_DATE, 18, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (85, 35, CURRENT_DATE, 18, 5);

-- Medicament 19: Doxycycline (id=19)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (75, 28, CURRENT_DATE, 19, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (115, 28, CURRENT_DATE, 19, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (35, 28, CURRENT_DATE, 19, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (175, 65, CURRENT_DATE, 19, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (70, 28, CURRENT_DATE, 19, 5);

-- Medicament 20: Metronidazole (id=20)
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (100, 40, CURRENT_DATE, 20, 1);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (145, 40, CURRENT_DATE, 20, 2);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (55, 40, CURRENT_DATE, 20, 3);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (210, 85, CURRENT_DATE, 20, 4);
INSERT INTO stock (quantite_disponible, seuil_alerte, datemaj, medicament_id, pharmacie_id) VALUES (95, 40, CURRENT_DATE, 20, 5);
