package com.empresa.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstitutionTest {

    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = new Institution();
    }

    @Test
    void testConstructorWithAllParameters() {
        Institution inst = new Institution("Test University", "TU", "Academic", 
                                          "USA", "New York", "https://test.edu", true, null);

        assertEquals("Test University", inst.getName());
        assertEquals("TU", inst.getAcronym());
        assertEquals("Academic", inst.getType());
        assertEquals("USA", inst.getCountry());
        assertEquals("New York", inst.getCity());
        assertEquals("https://test.edu", inst.getWebsite());
        assertTrue(inst.getIsHeadquarter());
        assertNull(inst.getParentInstitution());
    }

    @Test
    void testDefaultConstructor() {
        assertNull(institution.getId());
        assertNull(institution.getName());
        assertNull(institution.getAcronym());
        assertNull(institution.getType());
        assertNull(institution.getCountry());
        assertNull(institution.getCity());
        assertNull(institution.getWebsite());
        assertFalse(institution.getIsHeadquarter());
        assertNull(institution.getParentInstitution());
        assertNull(institution.getCreatedAt());
        assertNull(institution.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        institution.setId(1L);
        institution.setName("Test Institution");
        institution.setAcronym("TI");
        institution.setType("Research Institution");
        institution.setCountry("Canada");
        institution.setCity("Toronto");
        institution.setWebsite("https://test.ca");
        institution.setIsHeadquarter(true);
        institution.setParentInstitution("Parent Inst");

        assertEquals(1L, institution.getId());
        assertEquals("Test Institution", institution.getName());
        assertEquals("TI", institution.getAcronym());
        assertEquals("Research Institution", institution.getType());
        assertEquals("Canada", institution.getCountry());
        assertEquals("Toronto", institution.getCity());
        assertEquals("https://test.ca", institution.getWebsite());
        assertTrue(institution.getIsHeadquarter());
        assertEquals("Parent Inst", institution.getParentInstitution());
    }

    @Test
    void testEqualsAndHashCode() {
        Institution inst1 = new Institution("Test Uni", "TU", "Academic", 
                                           "USA", "NYC", "https://test.edu", true, null);
        Institution inst2 = new Institution("Test Uni", "TU", "Academic", 
                                           "USA", "NYC", "https://test.edu", true, null);
        Institution inst3 = new Institution("Other Uni", "OU", "Academic", 
                                           "Canada", "Toronto", "https://other.ca", false, "Test Uni");

        assertEquals(inst1, inst2);
        assertEquals(inst1.hashCode(), inst2.hashCode());
        
        assertNotEquals(inst1, inst3);
        assertNotEquals(inst1.hashCode(), inst3.hashCode());
    }

    @Test
    void testEqualsWithSameNameDifferentCountry() {
        Institution inst1 = new Institution("Test Uni", "TU", "Academic", 
                                           "USA", "NYC", "https://test.edu", true, null);
        Institution inst2 = new Institution("Test Uni", "TU", "Academic", 
                                           "Canada", "NYC", "https://test.ca", false, null);

        assertNotEquals(inst1, inst2);
    }

    @Test
    void testEqualsWithSameNameDifferentCity() {
        Institution inst1 = new Institution("Test Uni", "TU", "Academic", 
                                           "USA", "NYC", "https://test.edu", true, null);
        Institution inst2 = new Institution("Test Uni", "TU", "Academic", 
                                           "USA", "Boston", "https://test.edu", false, "Test Uni");

        assertNotEquals(inst1, inst2);
    }

    @Test
    void testToString() {
        institution.setId(1L);
        institution.setName("Test Institution");
        institution.setAcronym("TI");
        institution.setType("Academic");
        institution.setCountry("USA");
        institution.setCity("Boston");
        institution.setWebsite("https://test.edu");
        institution.setIsHeadquarter(true);

        String toString = institution.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Test Institution'"));
        assertTrue(toString.contains("acronym='TI'"));
        assertTrue(toString.contains("type='Academic'"));
        assertTrue(toString.contains("country='USA'"));
        assertTrue(toString.contains("city='Boston'"));
        assertTrue(toString.contains("website='https://test.edu'"));
        assertTrue(toString.contains("isHeadquarter=true"));
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(institution, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(institution, "String object");
    }

    @Test
    void testEqualsWithSameObject() {
        assertEquals(institution, institution);
    }
}