# Java Expense Tracker

A command-line expense management application built with Java, Maven and SQLite. It allows users to record income and expenses, manage transactions, track balances and generate monthly financial reports.

## Features

- Add income and expense transactions
- View all saved transactions
- Update and delete existing transactions
- Calculate total income, expenses and balance
- Generate monthly financial summaries
- Filter transactions by category and month
- Validate transaction information and handle invalid input
- Store transactions persistently using SQLite

## Technology Stack

- **Java 26:** Core application development
- **Maven:** Dependency management and build automation
- **SQLite:** Relational database
- **JDBC:** Database connectivity
- **JUnit 5:** Automated testing
- **Git and GitHub:** Version control

## Project Architecture

The application uses a layered structure:

- `model`: Transaction entities, categories and transaction types
- `database`: SQLite connection and table initialization
- `repository`: SQL operations for saving, retrieving, updating and deleting transactions
- `service`: Business logic, validation and financial calculations
- `Main.java`: Interactive command-line interface

## Getting Started

### Requirements

- Java 26 JDK
- Maven
- Git

### Installation

Clone the repository:

```bash
git clone YOUR_REPOSITORY_URL
```

Navigate into the project folder and run the automated tests:

```bash
mvn clean test
```

Run `Main.java` using an IDE such as IntelliJ IDEA.

The application initializes its SQLite database automatically when it starts.

## Application Menu

```text
===== EXPENSE TRACKER =====
1. Add Transaction
2. View All Transactions
3. View Balance
4. Update Transaction
5. Delete Transaction
6. Monthly Financial Report
7. Filter Transactions by Category
8. Filter Transactions by Month
9. Exit
```

## Testing

The project includes automated JUnit tests covering transaction validation, financial calculations, repository operations and SQLite integration.

## Future Improvements

- Spring Boot REST API
- Improved reporting and data export
- More comprehensive automated testing
- Web-based user interface

## Project Status

Functional command-line application. Further improvements and API development are planned.

## Author

**Kgosi Mogogole**

Computer Science Student | Aspiring Java Backend Developer