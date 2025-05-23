# Institution Management System

A modern Spring Boot application for managing academic institutions and their branches, built with Java 21.

## Features

- **Institution Management**: Create, read, update, and delete academic institutions
- **Branch Support**: Manage headquarters and branch locations separately
- **Validation**: Comprehensive business rule validation (no duplicate headquarters, no duplicate branches at same location)
- **Search & Filter**: Search institutions by name, filter by type, country, or headquarters/branch status
- **REST API**: Full RESTful API with JSON responses
- **Modern UI**: Responsive web interface built with Bootstrap 5 and Thymeleaf
- **Database**: H2 in-memory database with JPA/Hibernate
- **Testing**: 100% unit test coverage with integration tests

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6+ 

### Build and Run

```bash
# Clone and navigate to project
cd struts2-project

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on http://localhost:8080

### Alternative: Run with JAR

```bash
# Build executable JAR
mvn clean package

# Run the JAR
java -jar target/institution-management-1.0.0.jar
```

## Usage

### Web Interface

- **Add Institution**: http://localhost:8080/
- **View All Institutions**: http://localhost:8080/institutions
- **H2 Database Console**: http://localhost:8080/h2-console (dev only)

### REST API

The application provides a full REST API at `/api/institutions`:

- `GET /api/institutions` - Get all institutions
- `POST /api/institutions` - Create new institution
- `GET /api/institutions/{id}` - Get institution by ID
- `PUT /api/institutions/{id}` - Update institution
- `DELETE /api/institutions/{id}` - Delete institution
- `GET /api/institutions/search?query={term}` - Search institutions
- `GET /api/institutions/headquarters` - Get all headquarters
- `GET /api/institutions/branches/{name}` - Get branches for institution
- `GET /api/institutions/countries` - Get all countries
- `GET /api/institutions/types` - Get institution types

### Example API Request

```bash
curl -X POST http://localhost:8080/api/institutions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Stanford University",
    "acronym": "SU",
    "type": "Academic",
    "country": "USA",
    "city": "Stanford",
    "website": "https://stanford.edu",
    "headquarter": "Yes"
  }'
```

## Development

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Code Coverage

The project maintains 90%+ line coverage. Coverage reports are generated in `target/site/jacoco/`.

### Database

The application uses H2 in-memory database by default. Access the H2 console at http://localhost:8080/h2-console:

- **JDBC URL**: `jdbc:h2:mem:institutions`
- **Username**: `sa`
- **Password**: (empty)

### Development Mode

For development with auto-reload:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```

## Architecture

### Technology Stack

- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2**: Application framework
- **Spring Data JPA**: Database abstraction layer
- **Hibernate**: ORM implementation
- **H2 Database**: In-memory database for development
- **Thymeleaf**: Server-side templating engine
- **Bootstrap 5**: Frontend CSS framework
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **JaCoCo**: Code coverage analysis

### Package Structure

```
com.empresa/
├── entity/          # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic layer
├── controller/      # REST controllers
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
└── InstitutionManagementApplication.java
```

### Key Design Patterns

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Separation of API and domain models
- **Service Layer**: Business logic encapsulation
- **Global Exception Handling**: Centralized error management
- **Builder Pattern**: Fluent object construction

## Business Rules

1. **Headquarters Uniqueness**: Only one headquarters per institution name
2. **Branch Location Uniqueness**: No duplicate branches at the same country/city for an institution
3. **Data Validation**: All required fields must be provided with valid formats
4. **Referential Integrity**: Branches must reference valid parent institutions

## Migration from Struts 2

This application replaces the original Struts 2 implementation with modern Spring Boot architecture:

- **Actions → Controllers**: Struts Actions converted to Spring REST Controllers
- **JSP → Thymeleaf**: Server-side rendering with modern templating
- **File Storage → Database**: JSON file storage replaced with proper database
- **Manual Validation → Bean Validation**: Annotation-based validation
- **Custom DAOs → Spring Data**: Repositories with query methods

## Contributing

1. Ensure all tests pass: `mvn test`
2. Maintain code coverage above 90%
3. Follow existing code style and patterns
4. Add tests for new functionality

## License

This project is for educational and demonstration purposes.