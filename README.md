# CareSync Platform

CareSync is a healthcare backend platform designed to manage medical appointments and patient-related workflows.

The application is being developed as a modular backend system using Java and Spring Boot. The current implementation focuses on appointment management, user management, persistence, validation, and REST APIs.

## Current Features

### User Management

The application currently supports creating users with the following roles:

* `DOCTOR`
* `NURSE`
* `PATIENT`

Each user contains:

* Name
* Email
* Password
* Role

### Appointment Management

The application currently supports:

* Creating appointments
* Updating appointments
* Retrieving all appointments for a patient
* Retrieving only future appointments for a patient

Each appointment contains:

* Patient
* Doctor
* Date and time
* Notes
* Status

Available appointment statuses:

* `SCHEDULED`
* `COMPLETED`
* `CANCELLED`

## Current Architecture

The project currently follows a layered architecture:

```text
com.caresync.appointment
├── controller
├── dto
├── entity
├── repository
├── service
└── config
```

The main flow follows:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Current Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* Bean Validation
* Lombok
* Maven
* H2 Database

The project also already includes dependencies for:

* Spring GraphQL
* Spring AMQP / RabbitMQ

These integrations are not implemented yet.

## Database

The application currently uses an in-memory H2 database for development and testing.

Hibernate automatically creates the following tables when the application starts:

* `users`
* `appointments`

The database is currently configured as in-memory storage, so its data is reset whenever the application restarts.

## REST API

### Create User

```http
POST /api/users
```

### Create Appointment

```http
POST /api/appointments
```

### Update Appointment

```http
PUT /api/appointments/{id}
```

### Get Patient Appointments

```http
GET /api/appointments/patient/{patientId}
```

### Get Future Patient Appointments

```http
GET /api/appointments/patient/{patientId}/future
```

## Current Status

The current REST flow has been tested successfully using Postman.

The following flow is working:

```text
REST Request
    ↓
Controller
    ↓
Service
    ↓
Spring Data JPA Repository
    ↓
H2 Database
```

User creation, appointment creation, appointment updates, patient appointment history, and future appointment queries are currently functional.
