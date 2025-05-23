package com.empresa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "institutions", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name", "country", "city"})
       })
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 10, message = "Acronym must be less than 10 characters")
    private String acronym;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "Academic|Donor|NGO|Research Institution", 
             message = "Type must be one of: Academic, Donor, NGO, Research Institution")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @Pattern(regexp = "^https?://.*", message = "Website must start with http:// or https://")
    private String website;

    @Column(nullable = false)
    private Boolean isHeadquarter = false;

    @Column(name = "parent_institution")
    private String parentInstitution;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Institution() {}

    public Institution(String name, String acronym, String type, String country, 
                      String city, String website, Boolean isHeadquarter, String parentInstitution) {
        this.name = name;
        this.acronym = acronym;
        this.type = type;
        this.country = country;
        this.city = city;
        this.website = website;
        this.isHeadquarter = isHeadquarter;
        this.parentInstitution = parentInstitution;
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

    public Boolean getIsHeadquarter() {
        return isHeadquarter;
    }

    public void setIsHeadquarter(Boolean isHeadquarter) {
        this.isHeadquarter = isHeadquarter;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Institution that = (Institution) o;
        return Objects.equals(name, that.name) && 
               Objects.equals(country, that.country) && 
               Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, city);
    }

    @Override
    public String toString() {
        return "Institution{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", website='" + website + '\'' +
                ", isHeadquarter=" + isHeadquarter +
                ", parentInstitution='" + parentInstitution + '\'' +
                '}';
    }
}