package com.proxymedoc.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.Stock;
import com.proxymedoc.backend.repository.MedicamentRepository;
import com.proxymedoc.backend.repository.StockRepository;
import com.proxymedoc.backend.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicamentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(EntityDTOMapper.class)
class MedicamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicamentRepository medicamentRepository;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private SecurityUtil securityUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateMedicamentWithDenominationOnly() throws Exception {
        Medicament saved = new Medicament();
        saved.setId(1L);
        saved.setDenomination("Amoxicilline 500mg");
        saved.setPrixUnitaire(1500.0);
        saved.setDescription("Description test");
        saved.setCategorie("antibiotique");
        when(medicamentRepository.save(any(Medicament.class))).thenReturn(saved);

        mockMvc.perform(post("/api/medicaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "denomination": "Amoxicilline 500mg",
                          "prixUnitaire": 1500.0,
                          "description": "Description test",
                          "categorie": "antibiotique"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.denomination").value("Amoxicilline 500mg"))
                .andExpect(jsonPath("$.nom").doesNotExist());
    }

    @Test
    void shouldUpdateExistingMedicament() throws Exception {
        Medicament existing = new Medicament();
        existing.setId(10L);
        existing.setDenomination("Old name");
        existing.setPrixUnitaire(100.0);
        existing.setDescription("Ancienne description");
        existing.setCategorie("autre");

        when(medicamentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/medicaments/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "denomination": "Amoxicilline 500mg",
                          "prixUnitaire": 1500.0,
                          "description": "Description mise à jour",
                          "categorie": "antibiotique",
                          "dosage": "500mg"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.denomination").value("Amoxicilline 500mg"))
                .andExpect(jsonPath("$.prixUnitaire").value(1500.0))
                .andExpect(jsonPath("$.dosage").value("500mg"));
    }

    @Test
    void shouldCreateStockForAuthenticatedPharmacyWhenUploadingMedication() throws Exception {
        Medicament saved = new Medicament();
        saved.setId(2L);
        saved.setDenomination("Paracétamol 500mg");
        saved.setPrixUnitaire(250.0);
        saved.setDescription("Description test");
        saved.setCategorie("analgesique");

        Pharmacie pharmacy = new Pharmacie();
        pharmacy.setId(7L);

        Pharmacien pharmacist = new Pharmacien();
        pharmacist.setPharmacie(pharmacy);

        when(securityUtil.getCurrentUser()).thenReturn(pharmacist);
        when(medicamentRepository.save(any(Medicament.class))).thenReturn(saved);
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile photo = new MockMultipartFile("photo", "photo.png", MediaType.IMAGE_PNG_VALUE, "photo".getBytes());

        mockMvc.perform(multipart("/api/medicaments/with-files")
                .file(photo)
                .param("denomination", "Paracétamol 500mg")
                .param("categorie", "analgesique")
                .param("description", "Description test")
                .param("prixUnitaire", "250")
                .param("stock", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.denomination").value("Paracétamol 500mg"));

        verify(stockRepository).save(argThat(stock ->
                stock.getQuantiteDisponible() != null
                        && stock.getQuantiteDisponible().equals(15)
                        && stock.getPharmacie() != null
                        && stock.getPharmacie().getId().equals(7L)
                        && stock.getMedicament() != null
                        && stock.getMedicament().getId().equals(2L)
        ));
    }
}
