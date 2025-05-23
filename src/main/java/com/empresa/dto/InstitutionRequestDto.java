package com.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InstitutionRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 10, message = "Acronym must be less than 10 characters")
    private String acronym;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "Academic|Donor|NGO|Research Institution", 
             message = "Type must be one of: Academic, Donor, NGO, Research Institution")
    private String type;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "City is required")
    private String city;

    @Pattern(regexp = "^$|^https?://.*", message = "Website must start with http:// or https://")
    private String website;

    @NotBlank(message = "Headquarter field is required")
    @Pattern(regexp = "Yes|No", message = "Headquarter must be 'Yes' or 'No'")
    private String headquarter;

    private String institution; // Used for branches to specify parent institution

    // Constructors
    public InstitutionRequestDto() {}

    public InstitutionRequestDto(String name, String acronym, String type, String country, 
                                String city, String website, String headquarter, String institution) {
        this.name = name;
        this.acronym = acronym;
        this.type = type;
        this.country = country;
        this.city = city;
        this.website = website;
        this.headquarter = headquarter;
        this.institution = institution;
    }

    // Getters and Setters
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

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public boolean isHeadquarter() {
        return "Yes".equals(headquarter);
    }

    @Override
    public String toString() {
        return "InstitutionRequestDto{" +
                "name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", website='" + website + '\'' +
                ", headquarter='" + headquarter + '\'' +
                ", institution='" + institution + '\'' +
                '}';
    }
}