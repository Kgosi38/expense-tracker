# Java Expense Tracker

![Java CI](https://github.com/Kgosi38/expense-tracker/actions/workflows/java-ci.yml/badge.svg)

A full-stack expense management application built with **Java 26, Spring Boot 4, Maven, SQLite, HTML, CSS and JavaScript**.

The application helps users track income and expenses, manage financial transactions, calculate balances, generate monthly reports and visualize spending patterns.

It includes a **responsive web dashboard**, a **REST API**, and a **command-line interface (CLI)**.

## Features

### Financial Management
- Add income and expense transactions
- View, edit and delete transactions
- Calculate total income, expenses and available balance
- Filter transactions by category and month
- Sort and manage transaction history
- Store transaction data persistently using SQLite

### Web Dashboard
- Responsive interface built with HTML, CSS and JavaScript
- Transaction entry form and transaction history
- Financial balance overview
- Monthly financial reports
- Spending Analytics with category-based visualizations
- Automatic chart updates when transactions are added, edited or deleted

### Backend and API
- RESTful API built with Spring Boot
- Layered application architecture
- SQLite database integration using JDBC
- Transaction validation and error handling
- Monthly financial calculations
- Automated unit and integration testing

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 26 | Core application and business logic |
| Spring Boot 4 | REST API and backend configuration |
| Maven | Dependency management and build automation |
| SQLite | Relational database |
| JDBC | Database connectivity |
| HTML5 | Frontend structure |
| CSS3 | Responsive styling |
| JavaScript | Frontend functionality and API integration |
| JUnit 5 | Unit testing |
| Spring MockMvc | API integration testing |
| GitHub Actions | Continuous integration |
| Git and GitHub | Version control and project hosting |

## Project Architecture

The application follows a layered architecture to separate responsibilities.

- `model` - Transaction models, categories and transaction types
- `database` - SQLite database initialization
- `repository` - Database operations and persistence
- `service` - Business logic and financial calculations
- `controller` - REST API endpoints
- `dto` - API request data structures
- `config` - Application configuration
- `src/main/resources/static` - Web dashboard files
- `Main.java` - Command-line interface
- `ExpenseTrackerApplication.java` - Spring Boot entry point

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check application health |
| GET | `/api/transactions` | Retrieve all transactions |
| GET | `/api/transactions/balance` | Retrieve financial balance information |
| POST | `/api/transactions` | Create a transaction |
| PUT | `/api/transactions/{id}` | Update an existing transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| GET | `/api/transactions/report?month=YYYY-MM` | Generate a monthly financial report |
| GET | `/api/transactions/analytics?month=YYYY-MM` | Retrieve spending totals grouped by category |

### Example: Create a Transaction

**POST** `/api/transactions`

```json
{
  "type": "Expense",
  "amount": 150.00,
  "category": "Food",
  "description": "Lunch",
  "date": "2026-11-08"
}
```

A successful request creates a transaction and returns HTTP `201 Created`.

### Example: Spending Analytics

**GET** `/api/transactions/analytics?month=2026-11`

Example response:

```json
{
  "month": "2026-11",
  "categories": {
    "Food": 350.00,
    "Transport": 200.00,
    "Clothing": 500.00
  }
}
```

The web dashboard uses this endpoint to display category-based spending charts.

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

### Run the Web Application

Start `ExpenseTrackerApplication.java` from IntelliJ IDEA.

Alternatively, run:

```bash
mvn spring-boot:run
```

Open your browser and visit:

http://localhost:8080

The dashboard allows you to manage transactions, view financial reports and analyze monthly spending.

### Run the Command-Line Application

Run `Main.java` using IntelliJ IDEA.

The CLI provides an interactive menu for managing financial transactions.

## Automated Testing

The project currently includes **80 passing automated tests** covering backend functionality, database operations, business logic and REST API integration.

- Transaction model validation
- Business logic and financial calculations
- SQLite database initialization
- Repository CRUD operations
- REST API requests using Spring MockMvc
- Transaction creation, retrieval, updating and deletion
- Monthly spending calculations
- Spending Analytics API responses
- Invalid input handling
- HTTP error responses

Run all tests:

```bash
mvn clean test
```

GitHub Actions provides automated CI testing for the repository.

## Future Improvements

- User authentication and authorization
- CSV and PDF financial report exports
- API documentation using OpenAPI/Swagger
- Additional financial visualizations
- Expanded frontend automated testing
- Deployment to a public hosting environment

## Project Status

**Active Development**

The project currently has **80 passing automated tests**, with continuous integration configured through GitHub Actions.

The application currently includes a functional Java backend, SQLite persistence, REST API, command-line interface and responsive web dashboard.

Monthly financial reports and Spending Analytics are implemented and tested.

Additional improvements are planned.

## Author

**Kgosi Mogogole**

Computer Science Student | Aspiring Java Backend Developer

GitHub: https://github.com/Kgosi38
