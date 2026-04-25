# HPMS - Hiring Process Management System

## Overview

HPMS is a microservices-based backend system for managing the hiring process. It provides services for user management, job postings, applications, recommendations, email notifications, and more. Built with Spring Boot and Spring Cloud, it follows a distributed architecture using Eureka for service discovery and a gateway for routing.

## Architecture

The system is composed of the following modules:

- **Infrastructure**:
  - `config-server`: Centralized configuration management (Port: 8888)
  - `eurekaserver`: Service discovery server (Port: 8761)
  - `gateway`: API gateway for routing requests (Port: 8080)

- **Services**:
  - `userservice`: Manages user accounts and profiles
  - `jobservice`: Handles job postings and details
  - `applicationservice`: Manages job applications
  - `emailservice`: Sends email notifications
  - `recommendationservice`: Provides job recommendations
  - `referenceservice`: Manages references

- **Common**:
  - `common-lib`: Shared libraries and utilities

## Tech Stack

- **Java**: 25
- **Spring Boot**: 4.0.0
- **Spring Cloud**: 2025.1.0
- **Build Tool**: Maven
- **Database**: (Specify if known, e.g., PostgreSQL, MySQL)
- **Messaging**: (If applicable, e.g., RabbitMQ)

## Prerequisites

- Java 25
- Maven 3.6+
- (Any other dependencies like databases, message brokers)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd HPMS-Backend-Microservice
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Start the infrastructure services first:
   - Config Server: `cd infrastructure/config-server && mvn spring-boot:run`
   - Eureka Server: `cd infrastructure/eurekaserver && mvn spring-boot:run`
   - Gateway: `cd infrastructure/gateway && mvn spring-boot:run`

4. Start the microservices:
   - User Service: `cd services/userservice && mvn spring-boot:run`
   - Job Service: `cd services/jobservice && mvn spring-boot:run`
   - And so on for other services.

## API Endpoints

- Gateway: http://localhost:8080
- Config Server: http://localhost:8888
- Eureka Dashboard: http://localhost:8761

(Detail specific endpoints if available)

## Configuration

Configurations are managed via the Config Server. Check `infrastructure/config-server/src/main/resources/config/` for service-specific configs.

## Development

- Use the provided `mvnw` wrapper for consistent builds.
- IDE: IntelliJ IDEA recommended (`.idea` is ignored).

## Contributing

(Please add contribution guidelines)

## License

(Specify license)
