CREATE TABLE IF NOT EXISTS stock (
    id BIGSERIAL PRIMARY KEY,
    quantite_disponible INTEGER,
    seuil_alerte INTEGER,
    date_maj DATE,
    medicament_id BIGINT NOT NULL,
    pharmacie_id BIGINT NOT NULL,
    CONSTRAINT fk_stock_medicament FOREIGN KEY (medicament_id) REFERENCES medicament(id),
    CONSTRAINT fk_stock_pharmacie FOREIGN KEY (pharmacie_id) REFERENCES pharmacie(id)
);

ALTER TABLE IF EXISTS stock
    ALTER COLUMN medicament_id SET NOT NULL,
    ALTER COLUMN pharmacie_id SET NOT NULL;
