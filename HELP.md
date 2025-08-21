# 🚀 Task Scheduling Service - Setup & API Guide

This document provides detailed setup instructions and API usage examples for the Task Scheduling Service.

---

## 📋 Prerequisites

- Docker & Docker Compose
- Java 21+
- Maven 3.8+

---

## 🐳 Environment Setup

### 1. Set Up RocketMQ Environment

Check that `brokerIP1` in `broker.conf` is set to the local IP address:
   - Open `docker-compose.yml`
   - Make sure `broker.conf` contains `brokerIP1=<local IP>` and that it is configured correctly.

### 2. Start Required Services

```bash
# Start all services (MySQL, Redis, RocketMQ)
docker-compose up -d

# Check services status
docker-compose ps

### 3. Verify Services

- **MySQL**: `localhost:3306` (taskuser/taskpass)
- **Redis**: `localhost:6379`
- **RocketMQ NameServer**: `localhost:9876`
- **RocketMQ Broker**: `localhost:10911`
- **RocketMQ Console**: `http://localhost:8088`

### 4. Start Application

```bash
# Using Maven wrapper (Windows)
.\mvnw spring-boot:run

# Using Maven wrapper (Linux/Mac)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

---


## 🔧 API Usage Examples

### Base URL
```
http://localhost:8080
```

### 1️⃣ Create Scheduled Task

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type:' application/json" \
  -d '{
    "taskId": "abc-123",
    "executeAt": "2025-07-21T15:00:00Z",
    "payload": {
      "type": "email",
      "target": "hello@example.com",
      "message": "This is a scheduled task!"
    }
  }'
```

**Response:**

- status code: 201 Created
- Headers:
  - Location: `/tasks/{taskId}`

### 2️⃣ Get Task by ID

```bash
curl -X GET http://localhost:8080/tasks/abc-123
```

**Response:**
```json
{
  "taskId": "abc-123",
  "status": "PENDING",
  "scheduleAt": "2025-08-21T11:01:00Z",
  "payload": {
    "type": "email",
    "target": "hello@example.com",
    "message": "This is a scheduled task!"
  }
}
```

### 3️⃣ List Pending Tasks

```bash
# Get all pending tasks (default page=0, size=10)
curl -X GET "http://localhost:8080/tasks?status=PENDING&page=0&size=5"
```

**Response:**

```json
[
   {
      "id":5,
      "taskId":"abc-123",
      "payload":{
         "type":"email",
         "target":"hello@example.com",
         "message":"This is a scheduled task!"
      },
      "status":"PENDING",
      "executeAt":"2025-08-21T15:00:00Z",
      "createAt":"2025-08-21T03:37:52.428879Z",
      "updateAt":null
   }
   ...
]
```

### 4️⃣ Cancel Scheduled Task

```bash
curl -X DELETE http://localhost:8080/tasks/abc-123
```

**Response**

- status code: 204 No Content
