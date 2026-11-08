# Java Expense Tracker

A Java-based expense management application built using **Java 26, Spring Boot 4, Maven and SQLite**.

The application allows users to manage income and expenses, calculate financial balances, filter transactions and generate monthly financial summaries.

It supports both an interactive **command-line interface (CLI)** and a **REST API** for managing transactions.

## Features

- Add income and expense transactions
- View, update and delete transactions
- Calculate total income, expenses and available balance
- Generate monthly financial summaries
- Filter transactions by category and month
- Validate transaction information and handle invalid requests
- Store transaction data persistently using SQLite
- Manage transactions through REST API endpoints
- Run automated unit and integration tests

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 26 | Core application development |
| Spring Boot 4 | REST API and application configuration |
| Maven | Dependency management and build automation |
| SQLite | Relational database |
| JDBC | Database connectivity |
| JUnit 5 | Automated testing |
| Spring MockMvc | REST API integration testing |
| Git and GitHub | Version control |

## Project Architecture

The application follows a layered architecture:

- `model` - Transaction entities, categories and transaction types
- `database` - SQLite database initialization
- `repository` - Database CRUD operations
- `service` - Business logic, validation and financial calculations
- `controller` - REST API endpoints
- `dto` - API request data structures
- `config` - Spring application configuration
- `Main.java` - Interactive command-line interface
- `ExpenseTrackerApplication.java` - Spring Boot application entry point

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check application health |
| GET | `/api/transactions` | Retrieve all transactions |
| GET | `/api/transactions/balance` | Retrieve financial balance summary |
| POST | `/api/transactions` | Create a transaction |
| PUT | `/api/transactions/{id}` | Update an existing transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |

### Example: Create a Transaction

Send a POST request to `/api/transactions` with the following JSON:

```json
{
  "type": "Expense",
  "amount": 150.00,
  "category": "Food",
  "description": "Lunch",
  "date": "2026-11-08"
}
```

The API responds with the created transaction and HTTP status `201 Created`.

## Getting Started

### Requirements

- Java 26 JDK
- Maven
- Git

### Installation

Clone the repository:

```bash
git clone https://github.com/Kgosi38/expense-tracker.git
```

Navigate to the project directory:

```bash
cd expense-tracker
```

Run the automated tests:

```bash
mvn clean test
```

### Run the REST API

Start `ExpenseTrackerApplication.java` from IntelliJ IDEA.

By default, the REST API runs at:

`http://localhost:8080`

Example endpoint:

`http://localhost:8080/api/transactions`

### Run the Command-Line Application

Run `Main.java` using IntelliJ IDEA.

The application provides an interactive menu for managing transactions and viewing financial information.

## Automated Testing

The project currently includes **72 passing automated tests**.

Tests cover:

- Transaction model validation
- Business logic and financial calculations
- SQLite database initialization
- Repository CRUD operations
- REST API requests using MockMvc
- Transaction creation, retrieval, updating and deletion
- Invalid input handling
- HTTP 400 and 404 error responses

Run all tests using:

```bash
mvn clean test
```

## Future Improvements

- Web-based frontend
- User authentication and authorization
- CSV and PDF financial report exports
- Improved API validation and error handling
- API documentation using OpenAPI/Swagger
- Automated CI testing using GitHub Actions

## Project Status

**Active development**

The command-line application and Spring Boot REST API are functional. The project includes SQLite persistence and 72 passing automated tests.

Further improvements are planned.

## Author

**Kgosi Mogogole**

Computer Science Student | Aspiring Java Backend Developer

GitHub: https://github.com/Kgosi38
