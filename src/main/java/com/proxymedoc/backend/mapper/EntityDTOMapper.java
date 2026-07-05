package com.proxymedoc.backend.mapper;

import com.proxymedoc.backend.dto.*;
import com.proxymedoc.backend.model.*;
import org.springframework.stereotype.Component;

@Component
public class EntityDTOMapper {

    public Medicament toMedicament(MedicamentDTO dto) {
        if (dto == null) return null;
        Medicament m = new Medicament();
        m.setId(dto.getId());
        m.setDenomination(dto.getDenomination());
        m.setCategorie(dto.getCategorie());
        m.setDescription(dto.getDescription());
        m.setPrixUnitaire(dto.getPrixUnitaire());
        m.setFormeGalenique(dto.getFormeGalenique());
        m.setDosage(dto.getDosage());
        m.setExigeOrdonnance(dto.getExigeOrdonnance());
        m.setImageUrl(dto.getImageUrl());
        m.setNoticeUrl(dto.getNoticeUrl());
        return m;
    }

    public MedicamentDTO toMedicamentDTO(Medicament m) {
        if (m == null) return null;
        MedicamentDTO dto = new MedicamentDTO();
        dto.setId(m.getId());
        dto.setDenomination(m.getDenomination());
        dto.setCategorie(m.getCategorie());
        dto.setDescription(m.getDescription());
        dto.setPrixUnitaire(m.getPrixUnitaire());
        dto.setFormeGalenique(m.getFormeGalenique());
        dto.setDosage(m.getDosage());
        dto.setExigeOrdonnance(m.getExigeOrdonnance());
        dto.setImageUrl(m.getImageUrl());
        dto.setNoticeUrl(m.getNoticeUrl());
        return dto;
    }

    public Pharmacie toPharmace(PharmacieDTO dto) {
        if (dto == null) return null;
        Pharmacie p = new Pharmacie();
        p.setId(dto.getId());
        p.setNom(dto.getNom());
        p.setAdresse(dto.getAdresse());
        p.setLatitude(dto.getLatitude());
        p.setLongitude(dto.getLongitude());
        p.setTelephone(dto.getTelephone());
        p.setStatut(dto.getStatut() != null ? StatutPharmacie.valueOf(dto.getStatut()) : StatutPharmacie.EN_ATTENTE);
        p.setHoraires(dto.getHoraires());
        p.setEstDeGarde(dto.getEstDeGarde());
        p.setNumeroLicence(dto.getNumeroLicence());
        p.setEstActif(dto.getEstActif());
        p.setScoreIa(dto.getScoreIa());
        p.setContact(dto.getContact());
        return p;
    }

    public PharmacieDTO toPharmacieDTO(Pharmacie p) {
        if (p == null) return null;
        PharmacieDTO dto = new PharmacieDTO();
        dto.setId(p.getId());
        dto.setNom(p.getNom());
        dto.setAdresse(p.getAdresse());
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        dto.setTelephone(p.getTelephone());
        dto.setStatut(p.getStatut() != null ? p.getStatut().name() : null);
        dto.setHoraires(p.getHoraires());
        dto.setEstDeGarde(p.getEstDeGarde());
        dto.setNumeroLicence(p.getNumeroLicence());
        dto.setEstActif(p.getEstActif());
        dto.setScoreIa(p.getScoreIa());
        dto.setContact(p.getContact());
        return dto;
    }
}
