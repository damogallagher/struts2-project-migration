package com.empresa.controller;

import com.empresa.dto.InstitutionRequestDto;
import com.empresa.dto.InstitutionResponseDto;
import com.empresa.exception.DuplicateInstitutionException;
import com.empresa.exception.InstitutionNotFoundException;
import com.empresa.service.InstitutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstitutionController.class)
class InstitutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstitutionService institutionService;

    @Autowired
    private ObjectMapper objectMapper;

    private InstitutionRequestDto requestDto;
    private InstitutionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new InstitutionRequestDto();
        requestDto.setName("Test University");
        requestDto.setAcronym("TU");
        requestDto.setType("Academic");
        requestDto.setCountry("USA");
        requestDto.setCity("Boston");
        requestDto.setWebsite("https://test.edu");
        requestDto.setHeadquarter("Yes");

        responseDto = new InstitutionResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Test University");
        responseDto.setAcronym("TU");
        responseDto.setType("Academic");
        responseDto.setCountry("USA");
        responseDto.setCity("Boston");
        responseDto.setWebsite("https://test.edu");
        responseDto.setHeadquarter(true);
        responseDto.setCreatedAt(LocalDateTime.now());
        responseDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateInstitutionSuccess() throws Exception {
        when(institutionService.createInstitution(any(InstitutionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test University"))
                .andExpect(jsonPath("$.acronym").value("TU"))
                .andExpect(jsonPath("$.type").value("Academic"))
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.city").value("Boston"))
                .andExpect(jsonPath("$.website").value("https://test.edu"))
                .andExpect(jsonPath("$.headquarter").value(true));

        verify(institutionService).createInstitution(any(InstitutionRequestDto.class));
    }

    @Test
    void testCreateInstitutionValidationError() throws Exception {
        InstitutionRequestDto invalidRequest = new InstitutionRequestDto();
        invalidRequest.setName(""); // Invalid: empty name
        invalidRequest.setType("Invalid Type"); // Invalid: not in allowed values

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(institutionService, never()).createInstitution(any(InstitutionRequestDto.class));
    }

    @Test
    void testCreateInstitutionDuplicateError() throws Exception {
        when(institutionService.createInstitution(any(InstitutionRequestDto.class)))
                .thenThrow(new DuplicateInstitutionException("A headquarters already exists"));

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Institution"))
                .andExpect(jsonPath("$.message").value("A headquarters already exists"));

        verify(institutionService).createInstitution(any(InstitutionRequestDto.class));
    }

    @Test
    void testGetAllInstitutions() throws Exception {
        List<InstitutionResponseDto> institutions = Arrays.asList(responseDto);
        when(institutionService.getAllInstitutions()).thenReturn(institutions);

        mockMvc.perform(get("/api/institutions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test University"));

        verify(institutionService).getAllInstitutions();
    }

    @Test
    void testGetInstitutionByIdSuccess() throws Exception {
        when(institutionService.getInstitutionById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/institutions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test University"));

        verify(institutionService).getInstitutionById(1L);
    }

    @Test
    void testGetInstitutionByIdNotFound() throws Exception {
        when(institutionService.getInstitutionById(999L))
                .thenThrow(new InstitutionNotFoundException("Institution not found with ID: 999"));

        mockMvc.perform(get("/api/institutions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Institution Not Found"))
                .andExpect(jsonPath("$.message").value("Institution not found with ID: 999"));

        verify(institutionService).getInstitutionById(999L);
    }

    @Test
    void testUpdateInstitutionSuccess() throws Exception {
        when(institutionService.updateInstitution(eq(1L), any(InstitutionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/institutions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test University"));

        verify(institutionService).updateInstitution(eq(1L), any(InstitutionRequestDto.class));
    }

    @Test
    void testUpdateInstitutionNotFound() throws Exception {
        when(institutionService.updateInstitution(eq(999L), any(InstitutionRequestDto.class)))
                .thenThrow(new InstitutionNotFoundException("Institution not found with ID: 999"));

        mockMvc.perform(put("/api/institutions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(institutionService).updateInstitution(eq(999L), any(InstitutionRequestDto.class));
    }

    @Test
    void testDeleteInstitutionSuccess() throws Exception {
        doNothing().when(institutionService).deleteInstitution(1L);

        mockMvc.perform(delete("/api/institutions/1"))
                .andExpect(status().isNoContent());

        verify(institutionService).deleteInstitution(1L);
    }

    @Test
    void testDeleteInstitutionNotFound() throws Exception {
        doThrow(new InstitutionNotFoundException("Institution not found with ID: 999"))
                .when(institutionService).deleteInstitution(999L);

        mockMvc.perform(delete("/api/institutions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(institutionService).deleteInstitution(999L);
    }

    @Test
    void testSearchInstitutions() throws Exception {
        List<InstitutionResponseDto> institutions = Arrays.asList(responseDto);
        when(institutionService.searchInstitutionsByName("test")).thenReturn(institutions);

        mockMvc.perform(get("/api/institutions/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test University"));

        verify(institutionService).searchInstitutionsByName("test");
    }

    @Test
    void testGetAllHeadquarters() throws Exception {
        List<InstitutionResponseDto> headquarters = Arrays.asList(responseDto);
        when(institutionService.getAllHeadquarters()).thenReturn(headquarters);

        mockMvc.perform(get("/api/institutions/headquarters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].headquarter").value(true));

        verify(institutionService).getAllHeadquarters();
    }

    @Test
    void testGetBranchesByInstitution() throws Exception {
        InstitutionResponseDto branchDto = new InstitutionResponseDto();
        branchDto.setId(2L);
        branchDto.setName("Test University");
        branchDto.setCountry("Canada");
        branchDto.setCity("Toronto");
        branchDto.setHeadquarter(false);
        branchDto.setParentInstitution("Test University");

        List<InstitutionResponseDto> branches = Arrays.asList(branchDto);
        when(institutionService.getBranchesByInstitution("Test University")).thenReturn(branches);

        mockMvc.perform(get("/api/institutions/branches/Test University"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].headquarter").value(false))
                .andExpect(jsonPath("$[0].parentInstitution").value("Test University"));

        verify(institutionService).getBranchesByInstitution("Test University");
    }

    @Test
    void testGetCountries() throws Exception {
        mockMvc.perform(get("/api/institutions/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetUsedCountries() throws Exception {
        List<String> countries = Arrays.asList("USA", "Canada");
        when(institutionService.getDistinctCountries()).thenReturn(countries);

        mockMvc.perform(get("/api/institutions/countries/used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("USA"))
                .andExpect(jsonPath("$[1]").value("Canada"));

        verify(institutionService).getDistinctCountries();
    }

    @Test
    void testGetInstitutionTypes() throws Exception {
        mockMvc.perform(get("/api/institutions/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Academic"))
                .andExpect(jsonPath("$[1]").value("Donor"))
                .andExpect(jsonPath("$[2]").value("NGO"))
                .andExpect(jsonPath("$[3]").value("Research Institution"));
    }

    @Test
    void testGetUsedTypes() throws Exception {
        List<String> types = Arrays.asList("Academic", "Research Institution");
        when(institutionService.getDistinctTypes()).thenReturn(types);

        mockMvc.perform(get("/api/institutions/types/used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Academic"))
                .andExpect(jsonPath("$[1]").value("Research Institution"));

        verify(institutionService).getDistinctTypes();
    }
}