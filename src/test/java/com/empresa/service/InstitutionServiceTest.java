package com.empresa.service;

import com.empresa.dto.InstitutionRequestDto;
import com.empresa.dto.InstitutionResponseDto;
import com.empresa.entity.Institution;
import com.empresa.exception.DuplicateInstitutionException;
import com.empresa.exception.InstitutionNotFoundException;
import com.empresa.repository.InstitutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private InstitutionService institutionService;

    private InstitutionRequestDto headquarterRequest;
    private InstitutionRequestDto branchRequest;
    private Institution headquarterEntity;
    private Institution branchEntity;

    @BeforeEach
    void setUp() {
        headquarterRequest = new InstitutionRequestDto();
        headquarterRequest.setName("Test University");
        headquarterRequest.setAcronym("TU");
        headquarterRequest.setType("Academic");
        headquarterRequest.setCountry("USA");
        headquarterRequest.setCity("Boston");
        headquarterRequest.setWebsite("https://test.edu");
        headquarterRequest.setHeadquarter("Yes");

        branchRequest = new InstitutionRequestDto();
        branchRequest.setName("Test University");
        branchRequest.setAcronym("TU");
        branchRequest.setType("Academic");
        branchRequest.setCountry("Canada");
        branchRequest.setCity("Toronto");
        branchRequest.setWebsite("https://test.ca");
        branchRequest.setHeadquarter("No");
        branchRequest.setInstitution("Test University");

        headquarterEntity = new Institution();
        headquarterEntity.setId(1L);
        headquarterEntity.setName("Test University");
        headquarterEntity.setAcronym("TU");
        headquarterEntity.setType("Academic");
        headquarterEntity.setCountry("USA");
        headquarterEntity.setCity("Boston");
        headquarterEntity.setWebsite("https://test.edu");
        headquarterEntity.setIsHeadquarter(true);
        headquarterEntity.setCreatedAt(LocalDateTime.now());
        headquarterEntity.setUpdatedAt(LocalDateTime.now());

        branchEntity = new Institution();
        branchEntity.setId(2L);
        branchEntity.setName("Test University");
        branchEntity.setAcronym("TU");
        branchEntity.setType("Academic");
        branchEntity.setCountry("Canada");
        branchEntity.setCity("Toronto");
        branchEntity.setWebsite("https://test.ca");
        branchEntity.setIsHeadquarter(false);
        branchEntity.setParentInstitution("Test University");
        branchEntity.setCreatedAt(LocalDateTime.now());
        branchEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateHeadquarterSuccess() {
        when(institutionRepository.existsByNameAndIsHeadquarterTrue("Test University")).thenReturn(false);
        when(institutionRepository.save(any(Institution.class))).thenReturn(headquarterEntity);

        InstitutionResponseDto result = institutionService.createInstitution(headquarterRequest);

        assertNotNull(result);
        assertEquals("Test University", result.getName());
        assertEquals("TU", result.getAcronym());
        assertEquals("Academic", result.getType());
        assertEquals("USA", result.getCountry());
        assertEquals("Boston", result.getCity());
        assertTrue(result.isHeadquarter());
        assertNull(result.getParentInstitution());

        verify(institutionRepository).existsByNameAndIsHeadquarterTrue("Test University");
        verify(institutionRepository).save(any(Institution.class));
    }

    @Test
    void testCreateBranchSuccess() {
        when(institutionRepository.existsByNameAndCountryAndCity("Test University", "Canada", "Toronto")).thenReturn(false);
        when(institutionRepository.save(any(Institution.class))).thenReturn(branchEntity);

        InstitutionResponseDto result = institutionService.createInstitution(branchRequest);

        assertNotNull(result);
        assertEquals("Test University", result.getName());
        assertEquals("Canada", result.getCountry());
        assertEquals("Toronto", result.getCity());
        assertFalse(result.isHeadquarter());
        assertEquals("Test University", result.getParentInstitution());

        verify(institutionRepository).existsByNameAndCountryAndCity("Test University", "Canada", "Toronto");
        verify(institutionRepository).save(any(Institution.class));
    }

    @Test
    void testCreateHeadquarterDuplicateException() {
        when(institutionRepository.existsByNameAndIsHeadquarterTrue("Test University")).thenReturn(true);

        DuplicateInstitutionException exception = assertThrows(
            DuplicateInstitutionException.class,
            () -> institutionService.createInstitution(headquarterRequest)
        );

        assertTrue(exception.getMessage().contains("A headquarters already exists"));
        verify(institutionRepository).existsByNameAndIsHeadquarterTrue("Test University");
        verify(institutionRepository, never()).save(any(Institution.class));
    }

    @Test
    void testCreateBranchDuplicateException() {
        when(institutionRepository.existsByNameAndCountryAndCity("Test University", "Canada", "Toronto")).thenReturn(true);

        DuplicateInstitutionException exception = assertThrows(
            DuplicateInstitutionException.class,
            () -> institutionService.createInstitution(branchRequest)
        );

        assertTrue(exception.getMessage().contains("A branch already exists"));
        verify(institutionRepository).existsByNameAndCountryAndCity("Test University", "Canada", "Toronto");
        verify(institutionRepository, never()).save(any(Institution.class));
    }

    @Test
    void testGetAllInstitutions() {
        List<Institution> institutions = Arrays.asList(headquarterEntity, branchEntity);
        when(institutionRepository.findAll()).thenReturn(institutions);

        List<InstitutionResponseDto> result = institutionService.getAllInstitutions();

        assertEquals(2, result.size());
        assertEquals("Test University", result.get(0).getName());
        assertEquals("Test University", result.get(1).getName());
        assertTrue(result.get(0).isHeadquarter());
        assertFalse(result.get(1).isHeadquarter());

        verify(institutionRepository).findAll();
    }

    @Test
    void testGetInstitutionByIdSuccess() {
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(headquarterEntity));

        InstitutionResponseDto result = institutionService.getInstitutionById(1L);

        assertNotNull(result);
        assertEquals("Test University", result.getName());
        assertEquals(1L, result.getId());

        verify(institutionRepository).findById(1L);
    }

    @Test
    void testGetInstitutionByIdNotFound() {
        when(institutionRepository.findById(999L)).thenReturn(Optional.empty());

        InstitutionNotFoundException exception = assertThrows(
            InstitutionNotFoundException.class,
            () -> institutionService.getInstitutionById(999L)
        );

        assertTrue(exception.getMessage().contains("Institution not found with ID: 999"));
        verify(institutionRepository).findById(999L);
    }

    @Test
    void testUpdateInstitutionSuccess() {
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(headquarterEntity));
        when(institutionRepository.save(any(Institution.class))).thenReturn(headquarterEntity);

        InstitutionRequestDto updateRequest = new InstitutionRequestDto();
        updateRequest.setName("Updated University");
        updateRequest.setAcronym("UU");
        updateRequest.setType("Research Institution");
        updateRequest.setCountry("USA");
        updateRequest.setCity("Boston");
        updateRequest.setWebsite("https://updated.edu");
        updateRequest.setHeadquarter("Yes");

        InstitutionResponseDto result = institutionService.updateInstitution(1L, updateRequest);

        assertNotNull(result);
        verify(institutionRepository).findById(1L);
        verify(institutionRepository).save(any(Institution.class));
    }

    @Test
    void testUpdateInstitutionNotFound() {
        when(institutionRepository.findById(999L)).thenReturn(Optional.empty());

        InstitutionNotFoundException exception = assertThrows(
            InstitutionNotFoundException.class,
            () -> institutionService.updateInstitution(999L, headquarterRequest)
        );

        assertTrue(exception.getMessage().contains("Institution not found with ID: 999"));
        verify(institutionRepository).findById(999L);
        verify(institutionRepository, never()).save(any(Institution.class));
    }

    @Test
    void testDeleteInstitutionSuccess() {
        when(institutionRepository.existsById(1L)).thenReturn(true);

        institutionService.deleteInstitution(1L);

        verify(institutionRepository).existsById(1L);
        verify(institutionRepository).deleteById(1L);
    }

    @Test
    void testDeleteInstitutionNotFound() {
        when(institutionRepository.existsById(999L)).thenReturn(false);

        InstitutionNotFoundException exception = assertThrows(
            InstitutionNotFoundException.class,
            () -> institutionService.deleteInstitution(999L)
        );

        assertTrue(exception.getMessage().contains("Institution not found with ID: 999"));
        verify(institutionRepository).existsById(999L);
        verify(institutionRepository, never()).deleteById(999L);
    }

    @Test
    void testSearchInstitutionsByName() {
        List<Institution> institutions = Arrays.asList(headquarterEntity);
        when(institutionRepository.findByNameContainingIgnoreCase("test")).thenReturn(institutions);

        List<InstitutionResponseDto> result = institutionService.searchInstitutionsByName("test");

        assertEquals(1, result.size());
        assertEquals("Test University", result.get(0).getName());
        verify(institutionRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void testGetAllHeadquarters() {
        List<Institution> headquarters = Arrays.asList(headquarterEntity);
        when(institutionRepository.findByIsHeadquarterTrue()).thenReturn(headquarters);

        List<InstitutionResponseDto> result = institutionService.getAllHeadquarters();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isHeadquarter());
        verify(institutionRepository).findByIsHeadquarterTrue();
    }

    @Test
    void testGetBranchesByInstitution() {
        List<Institution> branches = Arrays.asList(branchEntity);
        when(institutionRepository.findByParentInstitutionAndIsHeadquarterFalse("Test University")).thenReturn(branches);

        List<InstitutionResponseDto> result = institutionService.getBranchesByInstitution("Test University");

        assertEquals(1, result.size());
        assertFalse(result.get(0).isHeadquarter());
        assertEquals("Test University", result.get(0).getParentInstitution());
        verify(institutionRepository).findByParentInstitutionAndIsHeadquarterFalse("Test University");
    }

    @Test
    void testGetDistinctCountries() {
        List<String> countries = Arrays.asList("USA", "Canada", "UK");
        when(institutionRepository.findDistinctCountries()).thenReturn(countries);

        List<String> result = institutionService.getDistinctCountries();

        assertEquals(3, result.size());
        assertTrue(result.contains("USA"));
        assertTrue(result.contains("Canada"));
        assertTrue(result.contains("UK"));
        verify(institutionRepository).findDistinctCountries();
    }

    @Test
    void testGetDistinctTypes() {
        List<String> types = Arrays.asList("Academic", "Research Institution");
        when(institutionRepository.findDistinctTypes()).thenReturn(types);

        List<String> result = institutionService.getDistinctTypes();

        assertEquals(2, result.size());
        assertTrue(result.contains("Academic"));
        assertTrue(result.contains("Research Institution"));
        verify(institutionRepository).findDistinctTypes();
    }
}