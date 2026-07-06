-- Insertion de médicaments supplémentaires pour tester la recherche
INSERT INTO medicament (denomination, categorie, description, prix_unitaire, forme_galenique, dosage, exige_ordonnance)
VALUES
    ('Paracétamol', 'Analgésique', 'Antipyrétique et analgésique léger', 480.0, 'Comprimé', '250mg', false),
    ('Amoxicilline', 'Antibiotique', 'Antibiotique bêta-lactamine', 2600.0, 'Comprimé', '1g', true);
