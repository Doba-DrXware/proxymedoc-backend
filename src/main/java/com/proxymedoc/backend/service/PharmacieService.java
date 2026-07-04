package com.proxymedoc.backend.service;

import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.StatutPharmacie;
import com.proxymedoc.backend.repository.PharmacieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PharmacieService {

    private final PharmacieRepository pharmacieRepository;

    public PharmacieService(PharmacieRepository pharmacieRepository) {
        this.pharmacieRepository = pharmacieRepository;
    }

    public List<Pharmacie> findAll() {
        return pharmacieRepository.findAll();
    }

    public Pharmacie findById(Long id) {
        return pharmacieRepository.findById(id).orElse(null);
    }

    public Pharmacie save(Pharmacie p) {
        return pharmacieRepository.save(p);
    }

    /**
     * Search pharmacies by name (case-insensitive partial match)
     */
    public List<Pharmacie> searchByName(String name) {
        return pharmacieRepository.findAll().stream()
                .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search pharmacies within radius (km) of given lat/lon
     */
    public List<Pharmacie> searchNearby(Double latitude, Double longitude, Double radiusKm) {
        return pharmacieRepository.findAll().stream()
                .filter(p -> isWithinRadius(p.getLatitude(), p.getLongitude(), latitude, longitude, radiusKm))
                .collect(Collectors.toList());
    }

    /**
     * Calculate distance between two points (Haversine formula)
     */
    public boolean isWithinRadius(Double lat1, Double lon1, Double lat2, Double lon2, Double radiusKm) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return false;
        }
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadiusKm * c;
        return distance <= radiusKm;
    }

    /**
     * Find validated pharmacies only
     */
    public List<Pharmacie> findValidated() {
        return pharmacieRepository.findAll().stream()
                .filter(p -> p.getStatut() == StatutPharmacie.VALIDEE)
                .collect(Collectors.toList());
    }
}
