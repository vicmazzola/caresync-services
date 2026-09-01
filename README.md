# CareSync Platform

CareSync is a healthcare backend platform designed to manage medical appointments, patient history, and asynchronous appointment notifications.

The application is being developed as a modular backend system using Java and Spring Boot.

The current implementation includes appointment management, user management, authentication and authorization, GraphQL queries, persistence, validation, and asynchronous messaging with RabbitMQ.

## Current Features

### User Management

The application supports creating users with the following roles:

* `DOCTOR`
* `NURSE`
* `PATIENT`

Each user contains:

* Name
* Email
* Password
* Role

Passwords are stored using BCrypt hashing.

---

### Security

The application uses Spring Security with HTTP Basic authentication.

Current access rules:

* Doctors can create and update appointments.
* Nurses can create and update appointments.
* Doctors and nurses can access patient appointment history.
* Patients can access only their own appointment history.
* Patients cannot create or update appointments.
* Unauthenticated requests to protected endpoints are rejected.

Patient ownership validation is also applied to GraphQL queries.

---

### Appointment Management

The application supports:

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

---

## GraphQL

GraphQL is available for flexible patient appointment queries.

Endpoint:

```http
POST /graphql
```

Available queries:

```graphql
patientAppointments(patientId: ID!)
```

Returns the complete appointment history for a patient.

```graphql
futurePatientAppointments(patientId: ID!)
```

Returns only future appointments for a patient.

GraphQL requests use the same authentication and patient ownership rules applied to the REST API.

---

## RabbitMQ Messaging

The appointment service publishes asynchronous events to RabbitMQ whenever an appointment is created or updated.

Current event types:

* `CREATED`
* `UPDATED`

RabbitMQ configuration:

```text
Exchange:
appointment.exchange

Routing Key:
appointment.notification

Queue:
appointment.notification.queue
```

Current event flow:

```text
Appointment API
      ↓
AppointmentService
      ↓
Appointment saved in database
      ↓
AppointmentEventPublisher
      ↓
RabbitMQ
      ↓
appointment.exchange
      ↓
appointment.notification
      ↓
appointment.notification.queue
```

The queue will later be consumed by the `notification-service`.

Appointment events are serialized as JSON.

---

## Current Architecture

The appointment service follows a layered architecture:

```text
com.caresync.appointment
├── config
├── controller
├── dto
├── entity
├── messaging
├── repository
├── security
├── service
└── AppointmentServiceApplication
```

The main REST flow follows:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

The asynchronous messaging flow follows:

```text
AppointmentService
    ↓
AppointmentEventPublisher
    ↓
RabbitMQ
```

---

## Current Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* Spring GraphQL
* Spring AMQP
* RabbitMQ
* Bean Validation
* BCrypt
* Lombok
* Maven
* H2 Database
* Docker (local RabbitMQ environment)

---

## Database

The application currently uses an in-memory H2 database for development and testing.

Hibernate automatically creates the following tables when the application starts:

* `users`
* `appointments`

The database is configured as in-memory storage, so its data is reset whenever the application fully restarts.

---

## REST API

### Create User

```http
POST /api/users
```

### Create Appointment

```http
POST /api/appointments
```

Requires:

* `DOCTOR`
* `NURSE`

### Update Appointment

```http
PUT /api/appointments/{id}
```

Requires:

* `DOCTOR`
* `NURSE`

### Get Patient Appointments

```http
GET /api/appointments/patient/{patientId}
```

Accessible by:

* `DOCTOR`
* `NURSE`
* `PATIENT`

Patients can access only their own appointments.

### Get Future Patient Appointments

```http
GET /api/appointments/patient/{patientId}/future
```

Accessible by:

* `DOCTOR`
* `NURSE`
* `PATIENT`

Patients can access only their own appointments.

---

## RabbitMQ Development Environment

RabbitMQ can currently be started locally using Docker:

```powershell
docker run -d `
  --name caresync-rabbitmq `
  -p 5672:5672 `
  -p 15672:15672 `
  rabbitmq:3-management
```

RabbitMQ Management UI:

```text
http://localhost:15672
```

Default development credentials:

```text
Username: guest
Password: guest
```

---

## Current Status

The following functionality has been tested successfully using Postman:

* User creation
* HTTP Basic authentication
* Role-based authorization
* Patient ownership authorization
* Appointment creation
* Appointment updates
* Patient appointment history
* Future appointment queries
* GraphQL appointment history queries
* GraphQL future appointment queries
* GraphQL patient ownership restrictions
* RabbitMQ appointment creation events
* RabbitMQ appointment update events

Current application flow:

```text
REST / GraphQL
      ↓
Security
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
H2 Database
```

Asynchronous flow:

```text
Create / Update Appointment
          ↓
    AppointmentService
          ↓
     RabbitMQ Event
          ↓
appointment.notification.queue
```

The next planned component is the `notification-service`, which will consume appointment events from RabbitMQ and process appointment reminders.
