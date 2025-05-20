# Banking Transactions API

A RESTful API for managing bank accounts and transactions, built with Spring Boot 3.3.2, Springdoc OpenAPI 2.6.0, and Java 21.0.7. The API supports creating accounts, transferring funds, retrieving account details, and viewing transaction histories, with robust validation and error handling.

## Features

- **Account Management**:
  - Create accounts with initial balance, first name, and last name (`POST /api/accounts`).
  - Retrieve account details (`GET /api/accounts/{accountId}`).
  - List all accounts (`GET /api/accounts`).
- **Transaction Management**:
  - Transfer funds between accounts (`POST /api/transactions`).
  - Retrieve transaction history for an account (`GET /api/accounts/{accountId}/transactions`).
  - Retrieve all transactions across all accounts (`GET /api/transactions`).
- **Validation**:
  - Balances and transaction amounts must have at most two decimal places (no rounding).
  - Mandatory fields: `firstName`, `lastName` for accounts; `fromAccountId`, `toAccountId`, `amount` for transactions.
  - Auto-generated fields: `id` (accounts and transactions), `timestamp` (transactions).
- **Swagger UI**:
  - Interactive API documentation at `http://localhost:8080/swagger-ui.html`.

## Prerequisites

- **Java**: JDK 21.0.7
- **Maven**: 3.8.6 or later
- **Git**: For cloning the repository
