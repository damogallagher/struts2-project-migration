package com.empresa.repository;

import com.empresa.entity.Institution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InstitutionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InstitutionRepository institutionRepository;

    private Institution headquarters;
    private Institution branch1;
    private Institution branch2;

    @BeforeEach
    void setUp() {
        // Create test data
        headquarters = new Institution("Test University", "TU", "Academic", 
                                     "USA", "Boston", "https://test.edu", true, null);
        
        branch1 = new Institution("Test University", "TU", "Academic", 
                                "Canada", "Toronto", "https://test.ca", false, "Test University");
        
        branch2 = new Institution("Test University", "TU", "Academic", 
                                "UK", "London", "https://test.co.uk", false, "Test University");

        // Persist test data
        entityManager.persistAndFlush(headquarters);
        entityManager.persistAndFlush(branch1);
        entityManager.persistAndFlush(branch2);
    }

    @Test
    void testFindByNameAndIsHeadquarterTrue() {
        Optional<Institution> found = institutionRepository.findByNameAndIsHeadquarterTrue("Test University");
        
        assertTrue(found.isPresent());
        assertEquals("Test University", found.get().getName());
        assertTrue(found.get().getIsHeadquarter());
        assertEquals("USA", found.get().getCountry());
    }

    @Test
    void testFindByNameAndIsHeadquarterTrueNotFound() {
        Optional<Institution> found = institutionRepository.findByNameAndIsHeadquarterTrue("Nonexistent University");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByName() {
        List<Institution> institutions = institutionRepository.findByName("Test University");
        
        assertEquals(3, institutions.size());
        assertTrue(institutions.stream().anyMatch(Institution::getIsHeadquarter));
        assertEquals(2, institutions.stream().mapToInt(i -> i.getIsHeadquarter() ? 0 : 1).sum());
    }

    @Test
    void testFindByNameAndCountryAndCity() {
        List<Institution> institutions = institutionRepository.findByNameAndCountryAndCity("Test University", "Canada", "Toronto");
        
        assertEquals(1, institutions.size());
        assertEquals("Canada", institutions.get(0).getCountry());
        assertEquals("Toronto", institutions.get(0).getCity());
        assertFalse(institutions.get(0).getIsHeadquarter());
    }

    @Test
    void testExistsByNameAndIsHeadquarterTrue() {
        assertTrue(institutionRepository.existsByNameAndIsHeadquarterTrue("Test University"));
        assertFalse(institutionRepository.existsByNameAndIsHeadquarterTrue("Nonexistent University"));
    }

    @Test
    void testExistsByNameAndCountryAndCity() {
        assertTrue(institutionRepository.existsByNameAndCountryAndCity("Test University", "Canada", "Toronto"));
        assertFalse(institutionRepository.existsByNameAndCountryAndCity("Test University", "France", "Paris"));
    }

    @Test
    void testFindByIsHeadquarterTrue() {
        List<Institution> headquarters = institutionRepository.findByIsHeadquarterTrue();
        
        assertEquals(1, headquarters.size());
        assertTrue(headquarters.get(0).getIsHeadquarter());
        assertEquals("USA", headquarters.get(0).getCountry());
    }

    @Test
    void testFindByParentInstitutionAndIsHeadquarterFalse() {
        List<Institution> branches = institutionRepository.findByParentInstitutionAndIsHeadquarterFalse("Test University");
        
        assertEquals(2, branches.size());
        assertTrue(branches.stream().allMatch(b -> !b.getIsHeadquarter()));
        assertTrue(branches.stream().allMatch(b -> "Test University".equals(b.getParentInstitution())));
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Institution> institutions = institutionRepository.findByNameContainingIgnoreCase("test");
        
        assertEquals(3, institutions.size());
        
        institutions = institutionRepository.findByNameContainingIgnoreCase("university");
        assertEquals(3, institutions.size());
        
        institutions = institutionRepository.findByNameContainingIgnoreCase("nonexistent");
        assertEquals(0, institutions.size());
    }

    @Test
    void testFindByCountry() {
        List<Institution> usInstitutions = institutionRepository.findByCountry("USA");
        assertEquals(1, usInstitutions.size());
        
        List<Institution> canadaInstitutions = institutionRepository.findByCountry("Canada");
        assertEquals(1, canadaInstitutions.size());
        
        List<Institution> franceInstitutions = institutionRepository.findByCountry("France");
        assertEquals(0, franceInstitutions.size());
    }

    @Test
    void testFindByType() {
        List<Institution> academicInstitutions = institutionRepository.findByType("Academic");
        assertEquals(3, academicInstitutions.size());
        
        List<Institution> ngoInstitutions = institutionRepository.findByType("NGO");
        assertEquals(0, ngoInstitutions.size());
    }

    @Test
    void testFindDistinctCountries() {
        List<String> countries = institutionRepository.findDistinctCountries();
        
        assertEquals(3, countries.size());
        assertTrue(countries.contains("USA"));
        assertTrue(countries.contains("Canada"));
        assertTrue(countries.contains("UK"));
        // Should be sorted
        assertEquals("Canada", countries.get(0));
        assertEquals("UK", countries.get(1));
        assertEquals("USA", countries.get(2));
    }

    @Test
    void testFindDistinctTypes() {
        List<String> types = institutionRepository.findDistinctTypes();
        
        assertEquals(1, types.size());
        assertTrue(types.contains("Academic"));
    }

    @Test
    void testFindDistinctTypesWithMultipleTypes() {
        // Add institution with different type
        Institution ngo = new Institution("Test NGO", "TNGO", "NGO", 
                                        "USA", "Seattle", "https://testngo.org", true, null);
        entityManager.persistAndFlush(ngo);
        
        List<String> types = institutionRepository.findDistinctTypes();
        
        assertEquals(2, types.size());
        assertTrue(types.contains("Academic"));
        assertTrue(types.contains("NGO"));
        // Should be sorted
        assertEquals("Academic", types.get(0));
        assertEquals("NGO", types.get(1));
    }

    @Test
    void testUniqueConstraint() {
        // Try to create duplicate institution (same name, country, city)
        Institution duplicate = new Institution("Test University", "TU2", "Research Institution", 
                                               "USA", "Boston", "https://test2.edu", false, "Test University");
        
        // This should fail due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicate);
        });
    }
}