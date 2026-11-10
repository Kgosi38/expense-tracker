
# Expense Tracker

A full-stack financial management application built with Java, Spring Boot, SQLite, HTML, CSS and JavaScript.

The application allows users to record income and expenses, manage transactions, generate monthly financial reports and explore spending analytics through a responsive web dashboard.

A public read-only demonstration is available with fictional financial data.

## Live Demo

**[Launch Expense Tracker](https://expense-tracker-7ro6.onrender.com)**

**[View GitHub Repository](https://github.com/Kgosi38/expense-tracker)**

The public demo allows visitors to explore the dashboard, search and filter transactions, and view financial reports without modifying data.

## Features

### Transaction Management
- Add income and expense transactions
- Edit existing transactions
- Delete transactions
- Categorise financial activity
- Store transaction details in SQLite

### Financial Dashboard
- Total income
- Total expenses
- Current balance
- Currency formatting in South African Rand (ZAR)

### Transaction History
- Search transactions by description
- Filter by transaction type
- Filter by category
- Sort by date or amount

### Monthly Financial Reports
- Select a reporting month
- Calculate monthly income
- Calculate monthly expenses
- Calculate monthly balance

### Spending Analytics
- Visualise expenses by category
- Compare spending amounts
- Analyse spending for selected months

### Read-Only Demo Mode
- Automatically loads fictional sample transactions
- Displays a demo information banner
- Hides transaction modification controls
- Prevents transaction creation, updates and deletion through backend restrictions
- Uses a separate SQLite demo database

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 26, Spring Boot 4 |
| Frontend | HTML5, CSS3, JavaScript |
| Database | SQLite |
| API | REST |
| Build Tool | Apache Maven |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Deployment | Docker, Render |
| Version Control | Git, GitHub |

## Architecture

The application follows a layered backend architecture:

- **Controller:** Handles HTTP requests and REST API responses
- **Service:** Implements business logic and financial calculations
- **Repository:** Handles SQLite database operations
- **Model:** Represents transactions and their properties
- **Frontend:** Communicates with the backend using JavaScript Fetch API

The application uses a separate Spring Boot profile for the public demonstration.

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check API health |
| GET | `/api/transactions` | Retrieve transactions |
| POST | `/api/transactions` | Create a transaction |
| PUT | `/api/transactions/{id}` | Update a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| GET | `/api/transactions/balance` | Retrieve financial totals |
| GET | `/api/transactions/report?month=YYYY-MM` | Retrieve monthly report |
| GET | `/api/transactions/analytics?month=YYYY-MM` | Retrieve spending analytics |
| GET | `/api/demo-mode` | Check whether demo mode is active |

Write operations are disabled when the demo profile is active.

## Running Locally

### Requirements

- Java 26
- Apache Maven
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Kgosi38/expense-tracker.git
cd expense-tracker
```

### 2. Run the automated tests

```bash
mvn clean test
```

### 3. Start the application

```bash
mvn spring-boot:run
```

Open:

http://localhost:8080

### 4. Run in demo mode

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo -Dspring-boot.run.arguments=--server.port=8081
```

Open:

http://localhost:8081

## Testing

The project includes automated backend tests covering transaction functionality and demo-mode restrictions.

Run:

```bash
mvn clean test
```

## Deployment

The application is containerised using a multi-stage Dockerfile and deployed as a Docker web service on Render.

The deployed application uses the `demo` Spring Boot profile.

**Note:** SQLite files on hosting services with ephemeral storage may be reset when an instance restarts or is redeployed. The public demonstration uses fictional data and is designed for read-only exploration.

## Future Improvements

- User authentication and account management
- PostgreSQL support for persistent cloud storage
- Budget planning and spending limits
- Exportable financial reports
- Additional automated and integration tests

## Author

**Kgosi Mogogole**

Computer Science Student | Aspiring Software Developer

GitHub: [Kgosi38](https://github.com/Kgosi38)
