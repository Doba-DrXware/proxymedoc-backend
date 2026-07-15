package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.StatutPharmacie;
import com.proxymedoc.backend.model.Stock;
import com.proxymedoc.backend.security.SecurityUtil;
import com.proxymedoc.backend.service.PharmacieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PharmacieController.class)
@AutoConfigureMockMvc(addFilters = false)
class PharmacieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PharmacieService pharmacieService;

    @MockBean
    private EntityDTOMapper mapper;

    @MockBean
    private SecurityUtil securityUtil;

    @Test
    void shouldExposeMedicationIdInPharmacyPayload() throws Exception {
        Pharmacie pharmacy = new Pharmacie();
        pharmacy.setId(7L);
        pharmacy.setNom("Pharmacie test");

        Medicament medicament = new Medicament();
        medicament.setId(99L);
        medicament.setDenomination("Amoxicilline 500mg");

        Stock stock = new Stock();
        stock.setQuantiteDisponible(10);
        stock.setMedicament(medicament);
        stock.setPharmacie(pharmacy);

        pharmacy.setStocks(List.of(stock));

        Pharmacien pharmacist = new Pharmacien();
        pharmacist.setPharmacie(pharmacy);

        when(securityUtil.getCurrentUser()).thenReturn(pharmacist);

        mockMvc.perform(get("/api/pharmacies/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meds[0].id").value(99));
    }

    @Test
    void shouldUpdatePharmacyStatus() throws Exception {
        Pharmacie pharmacy = new Pharmacie();
        pharmacy.setId(7L);
        pharmacy.setNom("Pharmacie test");
        pharmacy.setStatut(StatutPharmacie.EN_ATTENTE);

        when(pharmacieService.findById(7L)).thenReturn(pharmacy);
        when(pharmacieService.save(any(Pharmacie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/api/pharmacies/7/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"VALIDEE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pharmacy.statut").value("active"));
    }
}
