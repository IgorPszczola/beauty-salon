# BeeNails Booking Platform

A full-stack beauty salon booking platform with role-based access, appointment management, service catalog administration, and a polished customer-facing booking flow.

This project demonstrates end-to-end product thinking: backend API design, data modeling, auth and permissions, frontend UX, and practical testing.

## Highlights

- Role-based access control for CUSTOMER, STAFF, OWNER, and ADMIN
- Appointment booking flow with service and artist selection
- Calendar-driven appointment management for team members
- Service catalog management (create, edit, delete)
- User management tools (role updates, password reset, enable/disable)
- Validation and structured error handling on API requests
- Frontend-first UX improvements for booking and admin views

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.0.4
- Spring Web MVC
- Spring Data JPA + Hibernate
- Spring Security
- Bean Validation (Jakarta Validation)
- PostgreSQL (runtime)
- spring-dotenv for environment-based configuration

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript (no framework)
- Responsive layout and role-aware UI states

### Testing

- JUnit with Spring Boot test support
- H2 in-memory database for test profile
- Integration tests for critical API behavior

## Architecture Overview

- REST API backend under src/main/java/com/salon/booking
- Frontend pages under frontend/
- Role-aware UI with different capabilities for:
  - CUSTOMER: booking
  - STAFF: appointment operations
  - OWNER/ADMIN: full operations, including service and user management

## Project Structure

- src/main/java/...: Spring Boot application, controllers, services, repositories, DTOs
- src/main/resources/application.properties: runtime configuration (env-driven)
- src/test/...: test classes and test configuration
- frontend/: static frontend pages (index, booking, services, lookbook)
- pom.xml: Maven build and dependencies

## Setup

### 1. Prerequisites

- Java 21
- Maven (or use the included wrapper)
- PostgreSQL database

### 2. Configure Environment

Create a .env file in the project root:

DB_URL=jdbc:postgresql://localhost:5432/your_db
DB_USER=your_user
DB_PASSWORD=your_password

The app imports this via:

spring.config.import=optional:file:.env[.properties]

### 3. Run the Application

Windows:

mvnw.cmd spring-boot:run

Mac/Linux:

./mvnw spring-boot:run

Backend starts on:

http://localhost:8081

## Tests

Run tests with:

mvnw.cmd test

(or ./mvnw test on Mac/Linux)

Test profile uses H2 in-memory database with create-drop schema lifecycle.

## Key Functional Areas

### Booking Experience

- Select service, artist, date, and quick time slot
- Submit appointment request with validation
- Improved date/time UX (date picker + quick slots)

### Stuff Panel

- Calendar and day-level appointment details
- Appointment filtering by artist
- Assignment and status update workflows

### Organization Panel

- Service catalog operations
- User administration (role, password, enabled state)
- Dense table UI optimized for one-line records and quick actions

## API Scope (Representative)

- /api/auth/me
- /api/appointments
- /api/services
- /api/users

## Notes

- Server port is configured to 8081
- Production-style DB config is environment-driven
- Frontend and backend are intentionally decoupled for clear separation of concerns

## Possible Next Steps

- Add pagination/filtering to large tables
- Add E2E tests for booking and admin flows
- Add Docker Compose for one-command local startup
- Introduce CI pipeline with build + test checks
