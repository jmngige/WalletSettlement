# Wallet & Settlement Service

A Spring Boot microservice for **wallet management, settlement, and reconciliation**.  
Supports **balance top-ups, debits**, ledger recording, RabbitMQ message publishing, and automated reconciliation
against external CSV reports.

---

## Setup Instructions

### Prerequisites

- Docker Compose (v2.27.1+)
- JDK 17+
- Maven

### 1. Clone the Repository

```bash
[#git clone https://github.com/your-org/wallet-settlement.git](https://github.com/jmngige/WalletSettlement.git)
cd wallet-settlement
```

- Ensure the `init-db/01-schema.sql` file is present for database initialization.
- Reconciliation reports are stored in `src/main/resources/reconfiles/` and
- Reports output path ensure you provide one to your local path e.g `/home/path/Downloads` in the `.env` file on the
  projects root folder

### 2. Start Dependencies

Run MySQL, RabbitMQ, and the application using Docker Compose:

```bash
docker compose up -d
```

- **MySQL**: Accessible on `localhost:3308` (user: `wallet_user`, password: `wallet_password`, database: `wallet_db`).
- **RabbitMQ**: Accessible on `localhost:5672` (user: `rabbit_user`, password: `rabbit_password`). Management UI at
  `http://localhost:15672` (user: `rabbit_user`, password: `rabbit_password`).
- **Wallet App**: Accessible on `localhost:8085`.

### 3. Build and Run Locally (Without Docker)

1. Ensure MySQL and RabbitMQ are running locally or update `application.properties` with their connection details.

2. Build the project:

   ```bash
   mvn clean install
   ```

3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

### 4. Testing APIs

Use Swagger UI or `curl` to test the APIs.
You can access the endpoints at

 ```swagger 
 http://localhost:8085/swagger-ui/index.html
 ```

---

## API Documentation

### Wallet Endpoints

#### Create/Top-up Wallet

- **Endpoint**: `POST /wallets/{id}/topup`
- **Description**: Tops up the specified wallet with the given amount.
- **Request**:

```json
{
  "requestId": "req-MP-897",
  "transactionId": "ref-MP-899",
  "amount": 150
}
```

- **Response (Success)**:

```json
{
  "message": "Wallet top-up successful",
  "balance": 250.00,
  "status": "COMPLETED",
  "timestamp": "2025-09-10T11:19:46.128756724"
}
```

- **Response (Error - Duplicate Transaction)**:

```json
{
  "timestamp": "2025-09-10T11:19:51.447260845Z",
  "status": 409,
  "message": "Transaction already processed",
  "errorCode": "DUPLICATE_TRANSACTION"
}
```

#### Consume from Wallet

- **Endpoint**: `POST /wallets/{id}/consume`
- **Description**: Deducts the specified amount from the wallet.
- **Request**:

```json
{
  "requestId": "req-KYC-900",
  "transactionId": "ref-KYC-901",
  "amount": 50
}
```

- **Response (Success)**:

```json
{
  "message": "Request processed successfully",
  "requestId": "req-KYC-899",
  "status": "COMPLETED",
  "timestamp": "2025-09-10T11:23:51.145484312"
}
```

- **Response (Error - Insufficient Funds)**:

```json
{
  "timestamp": "2025-09-10T11:25:49.312856083Z",
  "status": 402,
  "message": "Insufficient wallet funds to process your request",
  "errorCode": "INSUFFICIENT_FUNDS"
}
```

- **Response (Error - Duplicate Transaction)**:

```json
{
  "timestamp": "2025-09-10T11:19:51.447260845Z",
  "status": 409,
  "message": "Transaction already processed",
  "errorCode": "DUPLICATE_TRANSACTION"
}
```

#### Get Wallet Balance

- **Endpoint**: `GET /wallets/{id}/balance`
- **Description**: Retrieves the current balance of the specified wallet.
- **Response**:

```json
{
  "walletId": "WLT-001",
  "balance": 1100,
  "timestamp": "2025-09-05T06:47:10.032Z"
}
```

---

### Reconciliation Endpoints

#### Generate Daily Reconciliation Report

- **Endpoint**: `GET api/v1/reconciliation/report?date=YYYY-MM-DD`
- **Description**: Reconciles internal ledger transactions with external CSV report (CRB, KYC, Credit Score).
- **Request**:

```
GET /api/v1/reconciliation/report?date=2025-09-04
```

- **Response**:

```json
[
  {
    "reconType": "KYC_CHECK",
    "transactionId": "ref-KYC-003",
    "amount": 45,
    "status": "Match",
    "description": "successful match"
  },
  {
    "reconType": "KYC_CHECK",
    "transactionId": "ref-KYC-003",
    "amount": 45,
    "status": "Match",
    "description": "successful match"
  },
  {
    "reconType": "CRB_CHECK",
    "transactionId": "ref-CRB-002",
    "amount": 50,
    "status": "Exception",
    "description": "CRB_CHECK transaction amount mismatch"
  },
  {
    "reconType": "CRB_CHECK",
    "transactionId": "ref-CRB-002",
    "amount": 55,
    "status": "Exception",
    "description": "CRB_CHECK transaction amount mismatch"
  },
  {
    "reconType": "CREDIT_SCORE",
    "transactionId": "ref-CS-006",
    "amount": 20,
    "status": "Exception",
    "description": "Missing external CREDIT_SCORE transaction"
  },
  {
    "reconType": "MPESA_TOPUP",
    "transactionId": "ref-MP-004",
    "amount": 350,
    "status": "Exception",
    "description": "Missing internal MPESA_TOPUP transaction"
  }
]
```

---

## Assumptions & Limitations

### Assumptions

- External reports are provided in CSV format, generated daily, containing CRB, KYC, and Credit Score transactions.
- Internal ledger transactions reflect the system's transactions.
- Transaction IDs are unique and used for matching between internal and external systems.

### Limitations

- No retry logic implemented for RabbitMQ message publishing.
- Reconciliation assumes a 1-to-1 transaction ID match between internal and external systems.

---

## Project Structure

```
src/main/java/com/presta/walletsettlement/
├── exception/            # Global exception handler
├── rabbbitmq/            # Publishes messages and queues them to RabbitMQ
├── reconciliation/       # CSV reader, reconciliation logic
├── wallet/               # Wallet, Ledger domain, repos, services
├── application.properties # App configs
├── application-test.properties # App test configs
└── docker-compose.yml
```

---

## Docker Compose Services

- **MySQL**: Persistent database for wallet and ledger data.
- **RabbitMQ**: Message broker for transaction events.
- **Wallet App**: Spring Boot application with REST APIs.

---
