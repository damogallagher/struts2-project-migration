package com.empresa.integration;

import com.empresa.InstitutionManagementApplication;
import com.empresa.dto.InstitutionRequestDto;
import com.empresa.dto.InstitutionResponseDto;
import com.empresa.entity.Institution;
import com.empresa.repository.InstitutionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = InstitutionManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InstitutionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        institutionRepository.deleteAll();
    }

    @Test
    void testCompleteInstitutionWorkflow() throws Exception {
        // 1. Create a headquarters
        InstitutionRequestDto headquarterRequest = new InstitutionRequestDto();
        headquarterRequest.setName("Test University");
        headquarterRequest.setAcronym("TU");
        headquarterRequest.setType("Academic");
        headquarterRequest.setCountry("USA");
        headquarterRequest.setCity("Boston");
        headquarterRequest.setWebsite("https://test.edu");
        headquarterRequest.setHeadquarter("Yes");

        MvcResult createResult = mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(headquarterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test University"))
                .andExpect(jsonPath("$.headquarter").value(true))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        InstitutionResponseDto createdHeadquarter = objectMapper.readValue(responseJson, InstitutionResponseDto.class);
        assertNotNull(createdHeadquarter.getId());

        // 2. Verify it exists in database
        assertTrue(institutionRepository.existsById(createdHeadquarter.getId()));

        // 3. Create a branch
        InstitutionRequestDto branchRequest = new InstitutionRequestDto();
        branchRequest.setName("Test University");
        branchRequest.setAcronym("TU");
        branchRequest.setType("Academic");
        branchRequest.setCountry("Canada");
        branchRequest.setCity("Toronto");
        branchRequest.setWebsite("https://test.ca");
        branchRequest.setHeadquarter("No");
        branchRequest.setInstitution("Test University");

        MvcResult branchResult = mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(branchRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test University"))
                .andExpect(jsonPath("$.headquarter").value(false))
                .andExpect(jsonPath("$.parentInstitution").value("Test University"))
                .andReturn();

        String branchResponseJson = branchResult.getResponse().getContentAsString();
        InstitutionResponseDto createdBranch = objectMapper.readValue(branchResponseJson, InstitutionResponseDto.class);

        // 4. Get all institutions
        mockMvc.perform(get("/api/institutions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 5. Get institution by ID
        mockMvc.perform(get("/api/institutions/" + createdHeadquarter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test University"))
                .andExpect(jsonPath("$.headquarter").value(true));

        // 6. Search institutions
        mockMvc.perform(get("/api/institutions/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 7. Get headquarters only
        mockMvc.perform(get("/api/institutions/headquarters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].headquarter").value(true));

        // 8. Get branches for institution
        mockMvc.perform(get("/api/institutions/branches/Test University"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].headquarter").value(false));

        // 9. Update institution
        InstitutionRequestDto updateRequest = new InstitutionRequestDto();
        updateRequest.setName("Updated University");
        updateRequest.setAcronym("UU");
        updateRequest.setType("Research Institution");
        updateRequest.setCountry("USA");
        updateRequest.setCity("Boston");
        updateRequest.setWebsite("https://updated.edu");
        updateRequest.setHeadquarter("Yes");

        mockMvc.perform(put("/api/institutions/" + createdHeadquarter.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated University"))
                .andExpect(jsonPath("$.type").value("Research Institution"));

        // 10. Delete institution
        mockMvc.perform(delete("/api/institutions/" + createdBranch.getId()))
                .andExpect(status().isNoContent());

        // 11. Verify deletion
        mockMvc.perform(get("/api/institutions/" + createdBranch.getId()))
                .andExpect(status().isNotFound());

        // 12. Verify only one institution remains
        mockMvc.perform(get("/api/institutions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testDuplicateHeadquarterValidation() throws Exception {
        // Create first headquarters
        InstitutionRequestDto firstRequest = new InstitutionRequestDto();
        firstRequest.setName("Duplicate University");
        firstRequest.setAcronym("DU");
        firstRequest.setType("Academic");
        firstRequest.setCountry("USA");
        firstRequest.setCity("Boston");
        firstRequest.setWebsite("https://duplicate.edu");
        firstRequest.setHeadquarter("Yes");

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Try to create duplicate headquarters
        InstitutionRequestDto duplicateRequest = new InstitutionRequestDto();
        duplicateRequest.setName("Duplicate University");
        duplicateRequest.setAcronym("DU2");
        duplicateRequest.setType("Research Institution");
        duplicateRequest.setCountry("Canada");
        duplicateRequest.setCity("Toronto");
        duplicateRequest.setWebsite("https://duplicate.ca");
        duplicateRequest.setHeadquarter("Yes");

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Institution"));
    }

    @Test
    void testDuplicateBranchValidation() throws Exception {
        // Create headquarters first
        InstitutionRequestDto headquarterRequest = new InstitutionRequestDto();
        headquarterRequest.setName("Branch Test University");
        headquarterRequest.setAcronym("BTU");
        headquarterRequest.setType("Academic");
        headquarterRequest.setCountry("USA");
        headquarterRequest.setCity("Boston");
        headquarterRequest.setWebsite("https://branchtest.edu");
        headquarterRequest.setHeadquarter("Yes");

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(headquarterRequest)))
                .andExpect(status().isCreated());

        // Create first branch
        InstitutionRequestDto firstBranchRequest = new InstitutionRequestDto();
        firstBranchRequest.setName("Branch Test University");
        firstBranchRequest.setAcronym("BTU");
        firstBranchRequest.setType("Academic");
        firstBranchRequest.setCountry("Canada");
        firstBranchRequest.setCity("Toronto");
        firstBranchRequest.setWebsite("https://branchtest.ca");
        firstBranchRequest.setHeadquarter("No");
        firstBranchRequest.setInstitution("Branch Test University");

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstBranchRequest)))
                .andExpect(status().isCreated());

        // Try to create duplicate branch at same location
        InstitutionRequestDto duplicateBranchRequest = new InstitutionRequestDto();
        duplicateBranchRequest.setName("Branch Test University");
        duplicateBranchRequest.setAcronym("BTU2");
        duplicateBranchRequest.setType("Research Institution");
        duplicateBranchRequest.setCountry("Canada");
        duplicateBranchRequest.setCity("Toronto");
        duplicateBranchRequest.setWebsite("https://branchtest2.ca");
        duplicateBranchRequest.setHeadquarter("No");
        duplicateBranchRequest.setInstitution("Branch Test University");

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateBranchRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Institution"));
    }

    @Test
    void testValidationErrors() throws Exception {
        // Test with invalid data
        InstitutionRequestDto invalidRequest = new InstitutionRequestDto();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setType("Invalid Type"); // Invalid type
        invalidRequest.setCountry(""); // Empty country
        invalidRequest.setCity(""); // Empty city
        invalidRequest.setHeadquarter("Maybe"); // Invalid headquarter value

        mockMvc.perform(post("/api/institutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void testGetCountriesEndpoint() throws Exception {
        mockMvc.perform(get("/api/institutions/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void testGetTypesEndpoint() throws Exception {
        mockMvc.perform(get("/api/institutions/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0]").value("Academic"))
                .andExpect(jsonPath("$[1]").value("Donor"))
                .andExpect(jsonPath("$[2]").value("NGO"))
                .andExpect(jsonPath("$[3]").value("Research Institution"));
    }
}