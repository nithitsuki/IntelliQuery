# 📡 API Documentation — NAME_PENDING

This REST API allows clients to interact with the **NAME_PENDING** natural language to SQL engine, powered by Spring Boot, JDBC, and a local LLM (Ollama).

It enables users to:
- Convert natural language queries into SQL
- Execute SQL queries against a MariaDB database
- Generate human-readable explanations of results

---

## 🔗 Base URL

```
http://localhost:8080/api
```

---

## 📤 Endpoints

### 🔍 `POST /query`

**Description**:  
Full natural language to SQL pipeline. Takes a user query, generates SQL, executes it, and explains the results.

**Request Body**:
```json
{
  "query": "Show me all employees who joined in the last 3 months"
}
```

**Response**:
```json
{
  "sql": "SELECT * FROM employees WHERE join_date >= CURDATE() - INTERVAL 3 MONTH",
  "result": [
    { "id": 1, "name": "Alice", "join_date": "2024-12-01" },
    ...
  ],
  "explanation": "This query finds employees whose joining date is within the last 3 months."
}
```

---

### 🧠 `POST /to-sql`

**Description**:  
Translates a natural language query into SQL, without executing it.

**Request Body**:
```json
{
  "query": "List all projects with a deadline this week"
}
```

**Response**:
```json
{
  "sql": "SELECT * FROM projects WHERE deadline BETWEEN CURDATE() AND CURDATE() + INTERVAL 7 DAY"
}
```

---

### ⚙️ `POST /execute`

**Description**:  
Executes a raw SQL query on the connected MariaDB instance.

**Request Body**:
```json
{
  "sql": "SELECT * FROM employees WHERE department = 'HR'"
}
```

**Response**:
```json
{
  "result": [
    { "id": 2, "name": "John Doe", "department": "HR" },
    ...
  ]
}
```

---

### 📖 `POST /explain`

**Description**:  
Explains SQL result data in plain English using the LLM.

**Request Body**:
```json
{
  "result": [
    { "name": "Alice", "salary": 80000 },
    { "name": "Bob", "salary": 95000 }
  ]
}
```

**Response**:
```json
{
  "explanation": "Alice and Bob are listed with their respective salaries. Bob has the highest salary in this group."
}
```

---

## 📌 Request Format

All endpoints accept and return JSON.

**Content-Type**:  
`application/json`

---

## 🔐 Authentication

Currently, no authentication is required for these endpoints. You may add API key or token-based auth in the future if deploying publicly.

---

## ⚠️ Error Handling

Standard HTTP status codes are used:

| Code | Meaning |
|------|---------|
| 200  | Success |
| 400  | Bad request (e.g., missing `query` field) |
| 500  | Server/LLM/DB error |

Example error:
```json
{
  "error": "Failed to translate query. Please check input or try again later."
}
```

---

## 📦 Sample `QueryRequest` Java Model

```java
public class QueryRequest {
    private String query;
    private String sql;
    private List<Map<String, Object>> result;

    // Getters & Setters
}
```

---