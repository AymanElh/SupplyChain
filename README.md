# SupplyChainX

A comprehensive Supply Chain Management system built with Spring Boot 3.5.7 and Java 21.

## 📋 Overview

SupplyChainX is a modern enterprise application designed to manage and optimize supply chain operations. The system provides features for managing users, deliveries, supplies, and production processes with role-based access control and comprehensive monitoring capabilities.

## ✨ Features

- **User Management**: Complete user lifecycle management with role-based authorization
- **Delivery Management**: Track and manage deliveries throughout the supply chain
- **Supply Management**: Manage inventory and supplies
- **Production Management**: Monitor and control production processes
- **Role-Based Access Control**: Secure endpoints with custom authorization aspects
- **RESTful API**: Well-documented REST API with OpenAPI/Swagger integration
- **Monitoring**: Built-in actuator endpoints for application health and metrics
- **Code Quality**: Integrated SonarQube for code quality analysis
- **Containerization**: Docker and Docker Compose support for easy deployment

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Spring Boot Starter Validation
- **Object Mapping**: MapStruct 1.5.5
- **API Documentation**: SpringDoc OpenAPI 2.8.13
- **Testing**: JUnit 5, Testcontainers
- **Code Coverage**: JaCoCo
- **Code Quality**: SonarQube
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## 📦 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL 18 (if running locally without Docker)

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/AymanElh/SupplyChain
cd SupplyChainX
```

### Running with Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose, which sets up all required services:

```bash
docker-compose up -d
```

This will start:
- **Application**: http://localhost:8080
- **PostgreSQL Database**: localhost:5432
- **PgAdmin**: http://localhost:5050
- **SonarQube**: http://localhost:9001

### Running Locally

1. **Start PostgreSQL Database**:
   ```bash
   docker-compose up -d db
   ```

2. **Configure Application**:
   Update `src/main/resources/application.yml` with your database credentials if needed.

3. **Build the Application**:
   ```bash
   ./mvnw clean install
   ```

4. **Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or with a specific profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## 🔧 Configuration

The application supports multiple profiles:

- **dev**: Development environment (`application-dev.yml`)
- **test**: Testing environment (`application-test.yml`)
- **prod**: Production environment (`application-prod.yml`)

Set the active profile using:
```bash
export SPRING_PROFILES_ACTIVE=dev
```

## 📚 API Documentation

Once the application is running, access the API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

Run tests with coverage report:

```bash
./mvnw clean test jacoco:report
```

The coverage report will be generated at: `target/site/jacoco/index.html`

## 📊 Code Quality

### Running SonarQube Analysis

1. Start SonarQube (if using Docker Compose):
   ```bash
   docker-compose up -d sonarqube
   ```

2. Access SonarQube at http://localhost:9001 (default credentials: admin/admin)

3. Run the analysis:
   ```bash
   ./mvnw clean verify sonar:sonar
   ```

## 🗄️ Database Access

### Using PgAdmin

1. Access PgAdmin at http://localhost:5050
2. Login with credentials:
   - Email: admin@admin.com
   - Password: admin
3. Connect to PostgreSQL:
   - Host: db
   - Port: 5432
   - Database: supply_db
   - Username: admin
   - Password: root

## 📁 Project Structure

```
SupplyChainX/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/ayman/supplychainx/
│   │   │       ├── common/          # Common utilities, config, exceptions
│   │   │       ├── user/            # User management module
│   │   │       ├── delivery/        # Delivery management module
│   │   │       ├── supply/          # Supply management module
│   │   │       ├── production/      # Production management module
│   │   │       └── SupplyChainXApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── application-test.yml
│   └── test/                        # Test classes
├── docker-compose.yaml              # Docker Compose configuration
├── Dockerfile                       # Application Dockerfile
├── pom.xml                         # Maven configuration
└── README.md
```

## 🔐 Security

The application implements role-based access control using custom `@RequiredRole` annotation. Ensure proper roles are assigned to users for accessing protected endpoints.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the terms specified in the `pom.xml` file.

## 👥 Authors

- Project developed by Ayman

## 📞 Support

For support and questions, please open an issue in the repository.

---

**Note**: Make sure to update default credentials and secrets before deploying to production.
