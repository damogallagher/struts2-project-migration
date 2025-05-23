package com.empresa.repository;

import com.empresa.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    /**
     * Find a headquarters institution by name
     */
    Optional<Institution> findByNameAndIsHeadquarterTrue(String name);

    /**
     * Find all institutions with the same name (headquarters and branches)
     */
    List<Institution> findByName(String name);

    /**
     * Find institutions by name and location
     */
    List<Institution> findByNameAndCountryAndCity(String name, String country, String city);

    /**
     * Check if a headquarters already exists for a given name
     */
    boolean existsByNameAndIsHeadquarterTrue(String name);

    /**
     * Check if a branch already exists at the same location for the same institution
     */
    boolean existsByNameAndCountryAndCity(String name, String country, String city);

    /**
     * Find all headquarters institutions
     */
    List<Institution> findByIsHeadquarterTrue();

    /**
     * Find all branches for a given institution name
     */
    List<Institution> findByParentInstitutionAndIsHeadquarterFalse(String parentInstitution);

    /**
     * Search institutions by name containing the search term (case insensitive)
     */
    @Query("SELECT i FROM Institution i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Institution> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Find institutions by country
     */
    List<Institution> findByCountry(String country);

    /**
     * Find institutions by type
     */
    List<Institution> findByType(String type);

    /**
     * Get all unique countries from institutions
     */
    @Query("SELECT DISTINCT i.country FROM Institution i ORDER BY i.country")
    List<String> findDistinctCountries();

    /**
     * Get all unique types from institutions
     */
    @Query("SELECT DISTINCT i.type FROM Institution i ORDER BY i.type")
    List<String> findDistinctTypes();
}