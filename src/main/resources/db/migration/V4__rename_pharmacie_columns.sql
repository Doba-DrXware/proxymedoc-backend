DO $$
BEGIN
	IF EXISTS (
		SELECT 1 FROM information_schema.columns
		WHERE table_name = 'pharmacie' AND column_name = 'document_legal_url'
	) THEN
		EXECUTE 'ALTER TABLE pharmacie RENAME COLUMN document_legal_url TO agrement_minsante';
	END IF;
END$$;

DO $$
BEGIN
	IF EXISTS (
		SELECT 1 FROM information_schema.columns
		WHERE table_name = 'pharmacie' AND column_name = 'fichier_url'
	) THEN
		EXECUTE 'ALTER TABLE pharmacie RENAME COLUMN fichier_url TO fichier_rc';
	END IF;
END$$;
