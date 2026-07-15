package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    Optional<Medicament> findByDenominationIgnoreCase(String denomination);

    /**
     * Recherche les médicaments en fonction d'une chaîne de recherche.
     * Cherche dans les champs: denomination, dosage, formeGalenique, categorie
     * 
     * @param searchQuery Chaîne de recherche (case-insensitive)
     * @return Liste des médicaments correspondants
     */
    @Query("SELECT m FROM Medicament m WHERE " +
            "LOWER(m.denomination) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.dosage) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.formeGalenique) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.categorie) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Medicament> search(@Param("query") String searchQuery);

    /**
     * Recherche les médicaments par dénomination exacte (ou partiellement).
     * 
     * @param denomination Dénomination à chercher
     * @return Liste des médicaments ayant cette dénomination
     */
    @Query("SELECT m FROM Medicament m WHERE LOWER(m.denomination) LIKE LOWER(CONCAT('%', :denomination, '%'))")
    List<Medicament> searchByDenomination(@Param("denomination") String denomination);

    /**
     * Recherche les médicaments par dosage.
     * 
     * @param dosage Dosage à chercher
     * @return Liste des médicaments ayant ce dosage
     */
    @Query("SELECT m FROM Medicament m WHERE m.dosage = :dosage")
    List<Medicament> searchByDosage(@Param("dosage") String dosage);

    /**
     * Recherche les médicaments par catégorie.
     * 
     * @param categorie Catégorie à chercher
     * @return Liste des médicaments ayant cette catégorie
     */
    @Query("SELECT m FROM Medicament m WHERE LOWER(m.categorie) LIKE LOWER(CONCAT('%', :categorie, '%'))")
    List<Medicament> searchByCategorie(@Param("categorie") String categorie);

    /**
     * Recherche les médicaments par forme galénique.
     * 
     * @param formeGalenique Forme galénique à chercher
     * @return Liste des médicaments ayant cette forme galénique
     */
    @Query("SELECT m FROM Medicament m WHERE LOWER(m.formeGalenique) LIKE LOWER(CONCAT('%', :formeGalenique, '%'))")
    List<Medicament> searchByFormeGalenique(@Param("formeGalenique") String formeGalenique);
}
