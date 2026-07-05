package com.proxymedoc.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.repository.MedicamentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
