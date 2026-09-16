# Wallet Service

A Spring Boot wallet service developed as part of a backend internship assignment.

## Features

- Credit Transactions
- Debit Transactions
- Prevents Duplicate Transactions using Transaction ID
- Handles Concurrent Requests using Pessimistic Locking
- Global Exception Handling
- H2 In-Memory Database
- Spring Data JPA
- Integration Tests

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- Hibernate
- H2 Database
- Maven

## API Endpoint

### Process Transaction

**POST** `/transactions`

### Example Request

```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "amount": 100.00,
  "type": "DEBIT"
}
```

## Running the Project

```bash
./mvnw spring-boot:run
```

## Running Tests

```bash
./mvnw test
```

## Design Decisions

See `DECISIONS.md` for:

- Concurrency handling
- Race condition prevention
- AI assistant reflection
