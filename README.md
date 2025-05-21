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


## Setup and Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/shrewdninja/BankingAPI-Java-SpringBoot.git
    cd BankingAPI-Java-SpringBoot
    ```

2. Verify Java and Maven:
    ```bash
    java -version  # Should output java 21.0.7
    mvn -version   # Should output Apache Maven 3.8.6 or later
    ```

3. Build the project:
    ```bash
    mvn clean install -U
    ```
    
4. Run the project:
    ```bash
    mvn spring-boot:run
    ```
    - The application starts on `http://localhost:8080` 
    - Logs are written to the console.


5. Access the application via Swagger UI:
    - Open `http://localhost:8080/swagger-ui.html` in a browser (use Incognito mode to avoid caching issues).
    - Explore endpoints, schemas, and test requests interactively
  
6. Test the endpoints on Swagger UI

## API Endpoints

| Method | Endpoint                              | Description                                      | Request Body Example                                                                 |
|--------|---------------------------------------|--------------------------------------------------|-------------------------------------------------------------------------------------|
| POST   | `/api/accounts`                      | Create a new account                             | `{"balance": 1000.00, "firstName": "John", "lastName": "Doe"}`                      |
| GET    | `/api/accounts`                      | List all accounts                               | N/A                                                                                 |
| GET    | `/api/accounts/{accountId}`          | Get account details                             | N/A                                                                                 |
| POST   | `/api/transactions`                  | Transfer funds between accounts                 | `{"fromAccountId": 1, "toAccountId": 2, "amount": 50.00}`                           |
| GET    | `/api/transactions`                  | List all transactions                           | N/A                                                                                 |
| GET    | `/api/accounts/{accountId}/transactions` | Get transaction history for an account       | N/A                                                                                 |
  
### Validation Rules

- **Accounts**:
  - `balance`: Required, zero or positive, at most two decimal places (e.g., `1000`, `1000.0`, `1000.00` valid; `1000.999` invalid).
  - `firstName`, `lastName`: Required, non-empty strings.
  - `id`: Auto-generated, must not be provided.
- **Transactions**:
  - `amount`: Required, positive, at most two decimal places.
  - `fromAccountId`, `toAccountId`: Required, must reference existing accounts.
  - `id`, `timestamp`: Auto-generated, must not be provided in requests.
- **Error Responses**:
  - `400 Bad Request`: Invalid input (e.g., `{"status": 400, "error": "Bad Request", "details": {"amount": "Amount must have at most two decimal places"}}`).
  - `404 Not Found`: Account not found.
  - `400 Bad Request`: Insufficient funds or same account transfer.

## Assumptions

The following assumptions were made during development:
- **In-Memory Storage**: The API uses an in-memory repository (`InMemoryBankingRepository`) for accounts and transactions, suitable for development and testing. No persistent database is configured.
- **No Authentication/Authorization**: The API is unsecured, assuming it’s for internal or development use. Production deployment would require Spring Security.
- **No Pagination**: Endpoints like `GET /api/transactions` and `GET /api/accounts` return all records without pagination or filtering, assuming small datasets.
- **BigDecimal Handling**:
  - Inputs do not contain leading or trailing 0s.
  - Balances and amounts are validated to have at most two decimal places without rounding (e.g., `100.999` triggers a `400 Bad Request`).
- **Timestamp Generation**: Transaction timestamps are generated server-side using `LocalDateTime.now()` and cannot be set by clients.
- **Swagger UI**: Used for API documentation and testing, accessible at `http://localhost:8080/swagger-ui.html`. Assumes Springdoc 2.6.0 is sufficient (upgrade to 2.8.0 recommended if rendering issues occur).
- **Error Handling**: Custom `ErrorResponse` DTO is used for `400`, `404`, and other errors, with detailed messages parsed from exceptions.
- **Development Environment**: Developed and tested on macOS with JDK 21.0.7 and Maven 3.8.6. Assumes compatibility with Linux/Windows but not explicitly tested.
- **Logging**: SLF4J with Logback is used for console logging, sufficient for debugging.

## Troubleshooting

1. Compilation Errors:
    ```bash
    mvn clean install -e -X  # Run Maven in debug mode
    ```
    - Ensure JDK 21.0.7 is installed and JAVA_HOME is configured correctly.

2. Swagger UI Testing:
    - Check console logs and response body for details on errors during API testing

## Development Notes

- **Dependencies**:
  - Spring Boot Starter Web, Validation, and Test.
  - Springdoc OpenAPI for Swagger UI.
  - SLF4J and Logback for logging.
- **Project Structure**:
  - `src/main/java/com/example/banking/controller`: REST controllers.
  - `src/main/java/com/example/banking/service`: Business logic.
  - `src/main/java/com/example/banking/repository`: Data access.
  - `src/main/java/com/example/banking/dto`: Data transfer objects.
