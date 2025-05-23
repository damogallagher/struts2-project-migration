package com.empresa.controller;

import com.empresa.dto.InstitutionRequestDto;
import com.empresa.dto.InstitutionResponseDto;
import com.empresa.service.InstitutionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionController.class);

    private final InstitutionService institutionService;

    @Autowired
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    /**
     * Create a new institution
     */
    @PostMapping
    public ResponseEntity<InstitutionResponseDto> createInstitution(@Valid @RequestBody InstitutionRequestDto requestDto) {
        logger.info("Creating new institution: {}", requestDto.getName());
        InstitutionResponseDto responseDto = institutionService.createInstitution(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Get all institutions
     */
    @GetMapping
    public ResponseEntity<List<InstitutionResponseDto>> getAllInstitutions() {
        logger.debug("Retrieving all institutions");
        List<InstitutionResponseDto> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.ok(institutions);
    }

    /**
     * Get institution by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponseDto> getInstitutionById(@PathVariable Long id) {
        logger.debug("Retrieving institution with ID: {}", id);
        InstitutionResponseDto institution = institutionService.getInstitutionById(id);
        return ResponseEntity.ok(institution);
    }

    /**
     * Update an existing institution
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponseDto> updateInstitution(
            @PathVariable Long id, 
            @Valid @RequestBody InstitutionRequestDto requestDto) {
        logger.info("Updating institution with ID: {}", id);
        InstitutionResponseDto responseDto = institutionService.updateInstitution(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Delete an institution
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitution(@PathVariable Long id) {
        logger.info("Deleting institution with ID: {}", id);
        institutionService.deleteInstitution(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search institutions by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<InstitutionResponseDto>> searchInstitutions(@RequestParam String query) {
        logger.debug("Searching institutions with query: {}", query);
        List<InstitutionResponseDto> institutions = institutionService.searchInstitutionsByName(query);
        return ResponseEntity.ok(institutions);
    }

    /**
     * Get all headquarters
     */
    @GetMapping("/headquarters")
    public ResponseEntity<List<InstitutionResponseDto>> getAllHeadquarters() {
        logger.debug("Retrieving all headquarters");
        List<InstitutionResponseDto> headquarters = institutionService.getAllHeadquarters();
        return ResponseEntity.ok(headquarters);
    }

    /**
     * Get branches for a specific institution
     */
    @GetMapping("/branches/{institutionName}")
    public ResponseEntity<List<InstitutionResponseDto>> getBranchesByInstitution(@PathVariable String institutionName) {
        logger.debug("Retrieving branches for institution: {}", institutionName);
        List<InstitutionResponseDto> branches = institutionService.getBranchesByInstitution(institutionName);
        return ResponseEntity.ok(branches);
    }

    /**
     * Get list of countries for dropdown
     */
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountries() {
        logger.debug("Retrieving countries list");
        // Get ISO countries
        String[] isoCountries = Locale.getISOCountries();
        List<String> countries = Arrays.stream(isoCountries)
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(countries);
    }

    /**
     * Get distinct countries from stored institutions
     */
    @GetMapping("/countries/used")
    public ResponseEntity<List<String>> getUsedCountries() {
        logger.debug("Retrieving used countries");
        List<String> countries = institutionService.getDistinctCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Get institution types for dropdown
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getInstitutionTypes() {
        logger.debug("Retrieving institution types");
        List<String> types = Arrays.asList("Academic", "Donor", "NGO", "Research Institution");
        return ResponseEntity.ok(types);
    }

    /**
     * Get distinct types from stored institutions
     */
    @GetMapping("/types/used")
    public ResponseEntity<List<String>> getUsedTypes() {
        logger.debug("Retrieving used types");
        List<String> types = institutionService.getDistinctTypes();
        return ResponseEntity.ok(types);
    }
}