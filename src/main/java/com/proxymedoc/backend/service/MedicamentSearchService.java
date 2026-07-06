package com.proxymedoc.backend.service;

import com.proxymedoc.backend.dto.MedicamentSearchDTO;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.repository.MedicamentRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service de recherche de médicaments.
 * Gère la logique de recherche des médicaments selon les critères de l'utilisateur.
 * Cette étape est indépendante de la pharmacie - elle cherche les médicaments candidats
 * qui seront plus tard optimisés selon les critères multicritères (distance, prix, stock, etc.)
 */
@Service
public class MedicamentSearchService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentSearchService(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    /**
     * Recherche générale de médicaments.
     * Cherche dans tous les champs pertinents: dénomination, dosage, forme galénique, catégorie.
     * 
     * La chaîne de recherche peut contenir:
     * - Nom du médicament (ex: "Paracétamol")
     * - Dosage (ex: "500mg")
     * - Forme galénique (ex: "comprimé")
     * - Catégorie (ex: "analgésique")
     * 
     * @param searchQuery Chaîne de recherche fournie par le patient
     * @return Liste des médicaments candidats avec informations essentielles
     */
    public List<MedicamentSearchDTO> searchMedicaments(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return List.of();
        }

        String trimmedQuery = searchQuery.trim();
        String normalizedQuery = normalize(trimmedQuery);
        List<String> tokens = List.of(normalizedQuery.split("\\s+"));
        List<String> nameTokens = tokens.stream()
                .filter(token -> !token.matches(".*\\d.*"))
                .toList();

        if (!nameTokens.isEmpty()) {
            List<Medicament> denominationMatches = findMedicamentsByDenominationTokens(nameTokens);
            if (!denominationMatches.isEmpty()) {
                return denominationMatches.stream()
                        .map(this::toSearchDTO)
                        .collect(Collectors.toList());
            }
        }

        List<Medicament> results = medicamentRepository.search(trimmedQuery);
        if (results.isEmpty()) {
            results = medicamentRepository.findAll().stream()
                    .filter(m -> matchesNormalizedSearch(m, normalizedQuery))
                    .collect(Collectors.toList());
        }

        return results.stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche avancée en cherchant d'abord par dénomination.
     * Cette méthode est utile si on veut une recherche hiérarchisée.
     * 
     * @param searchQuery Chaîne de recherche
     * @return Liste hiérarchisée: dénomination exacte en premier, puis autres critères
     */
    public List<MedicamentSearchDTO> searchMedicamentsAdvanced(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return List.of();
        }

        String trimmedQuery = searchQuery.trim();
        String normalizedQuery = normalize(trimmedQuery);

        List<Medicament> denominationMatches = medicamentRepository.searchByDenomination(trimmedQuery);
        if (denominationMatches.isEmpty()) {
            denominationMatches = medicamentRepository.findAll().stream()
                    .filter(m -> m.getDenomination() != null && normalize(m.getDenomination()).contains(normalizedQuery))
                    .collect(Collectors.toList());
        }

        List<Medicament> otherMatches = medicamentRepository.search(trimmedQuery);
        if (otherMatches.isEmpty()) {
            otherMatches = medicamentRepository.findAll().stream()
                    .filter(m -> matchesNormalizedSearch(m, normalizedQuery))
                    .collect(Collectors.toList());
        }

        var merged = new LinkedHashMap<Long, MedicamentSearchDTO>();
        denominationMatches.stream()
                .map(this::toSearchDTO)
                .forEach(dto -> merged.put(dto.getId(), dto));
        otherMatches.stream()
                .map(this::toSearchDTO)
                .forEach(dto -> merged.putIfAbsent(dto.getId(), dto));

        return merged.values().stream().collect(Collectors.toList());
    }

    /**
     * Recherche par dosage spécifique.
     * 
     * @param dosage Dosage à chercher (ex: "500mg")
     * @return Liste des médicaments avec ce dosage
     */
    public List<MedicamentSearchDTO> searchByDosage(String dosage) {
        if (dosage == null || dosage.trim().isEmpty()) {
            return List.of();
        }

        return medicamentRepository.searchByDosage(dosage).stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche par catégorie de médicament.
     * 
     * @param categorie Catégorie à chercher (ex: "Analgésique")
     * @return Liste des médicaments de cette catégorie
     */
    public List<MedicamentSearchDTO> searchByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            return List.of();
        }

        return medicamentRepository.searchByCategorie(categorie).stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche par forme galénique.
     * 
     * @param formeGalenique Forme galénique à chercher (ex: "Comprimé")
     * @return Liste des médicaments avec cette forme
     */
    public List<MedicamentSearchDTO> searchByFormeGalenique(String formeGalenique) {
        if (formeGalenique == null || formeGalenique.trim().isEmpty()) {
            return List.of();
        }

        String normalizedForm = normalize(formeGalenique);
        List<Medicament> results = medicamentRepository.searchByFormeGalenique(formeGalenique);
        if (results.isEmpty()) {
            results = medicamentRepository.findAll().stream()
                    .filter(m -> m.getFormeGalenique() != null && normalize(m.getFormeGalenique()).contains(normalizedForm))
                    .collect(Collectors.toList());
        }

        return results.stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    private boolean matchesNormalizedSearch(Medicament medicament, String normalizedQuery) {
        return Stream.of(medicament.getDenomination(), medicament.getDosage(), medicament.getFormeGalenique(), medicament.getCategorie())
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(value -> value.contains(normalizedQuery));
    }

    private List<Medicament> findMedicamentsByDenominationTokens(List<String> nameTokens) {
        return medicamentRepository.findAll().stream()
                .filter(m -> m.getDenomination() != null)
                .filter(m -> {
                    String normalizedDenomination = normalize(m.getDenomination());
                    return nameTokens.stream().allMatch(normalizedDenomination::contains);
                })
                .collect(Collectors.toList());
    }

    private String normalize(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim();
    }

    /**
     * Convertit une entité Medicament en DTO de recherche.
     * Ne garde que les champs essentiels pour l'identification et le filtrage.
     * 
     * @param medicament Entité Medicament
     * @return DTO pour la recherche
     */
    private MedicamentSearchDTO toSearchDTO(Medicament medicament) {
        return new MedicamentSearchDTO(
                medicament.getId(),
                medicament.getDenomination(),
                medicament.getCategorie(),
                medicament.getDosage(),
                medicament.getFormeGalenique()
        );
    }
}
