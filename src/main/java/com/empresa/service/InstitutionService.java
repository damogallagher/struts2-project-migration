package com.empresa.service;

import com.empresa.dto.InstitutionRequestDto;
import com.empresa.dto.InstitutionResponseDto;
import com.empresa.entity.Institution;
import com.empresa.exception.DuplicateInstitutionException;
import com.empresa.exception.InstitutionNotFoundException;
import com.empresa.repository.InstitutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitutionService {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionService.class);

    private final InstitutionRepository institutionRepository;

    @Autowired
    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    /**
     * Create a new institution or branch
     */
    public InstitutionResponseDto createInstitution(InstitutionRequestDto requestDto) {
        logger.info("Creating institution: {}", requestDto);

        // Validate business rules
        validateInstitutionRequest(requestDto);

        // Create entity from DTO
        Institution institution = new Institution();
        institution.setName(requestDto.getName());
        institution.setAcronym(requestDto.getAcronym());
        institution.setType(requestDto.getType());
        institution.setCountry(requestDto.getCountry());
        institution.setCity(requestDto.getCity());
        institution.setWebsite(requestDto.getWebsite());
        institution.setIsHeadquarter(requestDto.isHeadquarter());

        if (!requestDto.isHeadquarter()) {
            institution.setParentInstitution(requestDto.getInstitution());
        }

        // Save institution
        Institution savedInstitution = institutionRepository.save(institution);
        logger.info("Institution created successfully with ID: {}", savedInstitution.getId());

        return new InstitutionResponseDto(savedInstitution);
    }

    /**
     * Get all institutions
     */
    @Transactional(readOnly = true)
    public List<InstitutionResponseDto> getAllInstitutions() {
        logger.debug("Retrieving all institutions");
        return institutionRepository.findAll()
                .stream()
                .map(InstitutionResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get institution by ID
     */
    @Transactional(readOnly = true)
    public InstitutionResponseDto getInstitutionById(Long id) {
        logger.debug("Retrieving institution with ID: {}", id);
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new InstitutionNotFoundException("Institution not found with ID: " + id));
        return new InstitutionResponseDto(institution);
    }

    /**
     * Update an existing institution
     */
    public InstitutionResponseDto updateInstitution(Long id, InstitutionRequestDto requestDto) {
        logger.info("Updating institution with ID: {}", id);

        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new InstitutionNotFoundException("Institution not found with ID: " + id));

        // Update fields
        institution.setName(requestDto.getName());
        institution.setAcronym(requestDto.getAcronym());
        institution.setType(requestDto.getType());
        institution.setCountry(requestDto.getCountry());
        institution.setCity(requestDto.getCity());
        institution.setWebsite(requestDto.getWebsite());
        institution.setIsHeadquarter(requestDto.isHeadquarter());

        if (!requestDto.isHeadquarter()) {
            institution.setParentInstitution(requestDto.getInstitution());
        } else {
            institution.setParentInstitution(null);
        }

        Institution updatedInstitution = institutionRepository.save(institution);
        logger.info("Institution updated successfully: {}", updatedInstitution.getId());

        return new InstitutionResponseDto(updatedInstitution);
    }

    /**
     * Delete an institution
     */
    public void deleteInstitution(Long id) {
        logger.info("Deleting institution with ID: {}", id);

        if (!institutionRepository.existsById(id)) {
            throw new InstitutionNotFoundException("Institution not found with ID: " + id);
        }

        institutionRepository.deleteById(id);
        logger.info("Institution deleted successfully: {}", id);
    }

    /**
     * Search institutions by name
     */
    @Transactional(readOnly = true)
    public List<InstitutionResponseDto> searchInstitutionsByName(String searchTerm) {
        logger.debug("Searching institutions with term: {}", searchTerm);
        return institutionRepository.findByNameContainingIgnoreCase(searchTerm)
                .stream()
                .map(InstitutionResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all headquarters
     */
    @Transactional(readOnly = true)
    public List<InstitutionResponseDto> getAllHeadquarters() {
        logger.debug("Retrieving all headquarters");
        return institutionRepository.findByIsHeadquarterTrue()
                .stream()
                .map(InstitutionResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get branches for a given institution
     */
    @Transactional(readOnly = true)
    public List<InstitutionResponseDto> getBranchesByInstitution(String institutionName) {
        logger.debug("Retrieving branches for institution: {}", institutionName);
        return institutionRepository.findByParentInstitutionAndIsHeadquarterFalse(institutionName)
                .stream()
                .map(InstitutionResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get unique countries
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCountries() {
        return institutionRepository.findDistinctCountries();
    }

    /**
     * Get unique types
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctTypes() {
        return institutionRepository.findDistinctTypes();
    }

    /**
     * Validate business rules for institution creation
     */
    private void validateInstitutionRequest(InstitutionRequestDto requestDto) {
        if (requestDto.isHeadquarter()) {
            // Check if headquarters already exists for this name
            if (institutionRepository.existsByNameAndIsHeadquarterTrue(requestDto.getName())) {
                throw new DuplicateInstitutionException("A headquarters already exists for institution: " + requestDto.getName());
            }
        } else {
            // Check if branch already exists at the same location
            if (institutionRepository.existsByNameAndCountryAndCity(
                    requestDto.getName(), requestDto.getCountry(), requestDto.getCity())) {
                throw new DuplicateInstitutionException(
                        "A branch already exists for " + requestDto.getName() + 
                        " in " + requestDto.getCity() + ", " + requestDto.getCountry());
            }
        }
    }
}