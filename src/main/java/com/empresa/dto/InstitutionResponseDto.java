package com.empresa.dto;

import com.empresa.entity.Institution;

import java.time.LocalDateTime;

public class InstitutionResponseDto {

    private Long id;
    private String name;
    private String acronym;
    private String type;
    private String country;
    private String city;
    private String website;
    private boolean isHeadquarter;
    private String parentInstitution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public InstitutionResponseDto() {}

    public InstitutionResponseDto(Institution institution) {
        this.id = institution.getId();
        this.name = institution.getName();
        this.acronym = institution.getAcronym();
        this.type = institution.getType();
        this.country = institution.getCountry();
        this.city = institution.getCity();
        this.website = institution.getWebsite();
        this.isHeadquarter = institution.getIsHeadquarter();
        this.parentInstitution = institution.getParentInstitution();
        this.createdAt = institution.getCreatedAt();
        this.updatedAt = institution.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isHeadquarter() {
        return isHeadquarter;
    }

    public void setHeadquarter(boolean headquarter) {
        isHeadquarter = headquarter;
    }

    public String getParentInstitution() {
        return parentInstitution;
    }

    public void setParentInstitution(String parentInstitution) {
        this.parentInstitution = parentInstitution;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "InstitutionResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", website='" + website + '\'' +
                ", isHeadquarter=" + isHeadquarter +
                ", parentInstitution='" + parentInstitution + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}