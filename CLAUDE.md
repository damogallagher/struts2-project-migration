# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

- **Build project**: `mvn clean install`
- **Run application**: `mvn spring-boot:run` (starts on port 8080)
- **Run tests**: `mvn test`
- **Run tests with coverage**: `mvn test jacoco:report`
- **Clean project**: `mvn clean`
- **Package JAR**: `mvn clean package`
- **Run JAR**: `java -jar target/institution-management-1.0.0.jar`

## Architecture Overview

This is a modern Spring Boot 3.2 application built with Java 21 for managing academic institutions and their branches.

### Technology Stack

- **Java 21**: Latest LTS with modern language features
- **Spring Boot 3.2**: Main application framework
- **Spring Data JPA**: Database abstraction with Hibernate
- **H2 Database**: In-memory database for development
- **Thymeleaf**: Server-side templating engine
- **Bootstrap 5**: Frontend CSS framework
- **JUnit 5 + Mockito**: Testing framework
- **JaCoCo**: Code coverage analysis (90%+ required)

### Package Structure

```
com.empresa/
├── entity/          # JPA entities (Institution)
├── repository/      # Spring Data repositories
├── service/         # Business logic layer
├── controller/      # REST controllers + Web controllers
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions + global handler
└── InstitutionManagementApplication.java
```

### Key Components

- **Institution Entity**: JPA entity with validation annotations, unique constraints
- **InstitutionRepository**: Spring Data repository with custom query methods
- **InstitutionService**: Business logic with transaction management
- **InstitutionController**: REST API endpoints (/api/institutions/*)
- **WebController**: Serves Thymeleaf templates (/, /institutions)
- **GlobalExceptionHandler**: Centralized error handling

### Business Logic

The application manages academic institutions with these rules:
1. **Headquarters**: Only one headquarters per institution name
2. **Branches**: Multiple branches allowed but not at same country/city combination
3. **Validation**: Bean validation for all inputs
4. **Types**: Academic, Donor, NGO, Research Institution

### Database Schema

- **institutions** table with columns: id, name, acronym, type, country, city, website, is_headquarter, parent_institution, created_at, updated_at
- **Unique constraint**: (name, country, city)

### API Endpoints

REST API at `/api/institutions/`:
- CRUD operations (GET, POST, PUT, DELETE)
- Search functionality
- Filtering by type, country, headquarters status
- Utility endpoints for countries and types

### Frontend

- **Thymeleaf templates**: layout.html, index.html, institutions.html
- **Bootstrap 5**: Responsive design
- **AJAX**: Dynamic content loading with jQuery
- **DataTables**: Advanced table functionality

### Testing Strategy

- **Unit Tests**: All layers tested individually with Mockito
- **Integration Tests**: Full application context testing
- **Repository Tests**: @DataJpaTest for database operations
- **Controller Tests**: @WebMvcTest for HTTP layer
- **Coverage**: JaCoCo enforces 90%+ line coverage

### Configuration

- **application.properties**: Main configuration
- **application-test.properties**: Test-specific settings
- **H2 Console**: http://localhost:8080/h2-console (dev only)

### Migration Notes

This project was migrated from Struts 2 to Spring Boot:
- Struts Actions → Spring Controllers
- JSP → Thymeleaf templates
- File storage → JPA/Database
- Manual validation → Bean validation
- Custom DAOs → Spring Data repositories