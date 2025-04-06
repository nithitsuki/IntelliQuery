# 🧠 NAME_PENDING — Natural Language to SQL for Non-Tech Users

NAME_PENDING is an intelligent Java-powered backend system that allows non-technical users to query relational databases using **natural language**.

It uses a large language model to:
1. Translate natural language into SQL queries
2. Run those queries on a MariaDB database using JDBC
3. Explain the results back in plain English

Built using **Spring Boot** and **OkHttp**, NAME_PENDING supports both:
- A powerful **CLI interface** for immediate querying
- A clean **REST API** that can be consumed by any future frontend
- We might need to make a web front-end if the teach asks for it

---

## Possible Candidates for the name

- Project MonzAI 
- MonzaSentience
- MonzaMind
- NEXUS by Monza

---

## 🔧 Technologies Used

- Java 17
- Maven
- Spring Boot
- JDBC (MariaDB)
- OkHttp
- Ollama LLM API
- REST API for frontend/CLI support

---

## 🎯 Target Users

This tool is designed for **non-technical professionals** who interact with structured data:
- HR representatives
- Office administrators
- Receptionists
- Project managers

Users can interact with complex databases **without writing a single line of SQL**.

---

## 🧭 Project Structure

```
monza-core/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── monza/
│   │   │       └── devs/
│   │   │           ├── App.java                  # CLI entry point
│   │   │           ├── config/
│   │   │           │   └── DatabaseConfig.java   # Spring DB configuration
│   │   │           ├── controller/
│   │   │           │   └── QueryController.java  # REST endpoints
│   │   │           ├── service/
│   │   │           │   ├── LLMService.java       # Interacts with Ollama
│   │   │           │   └── DatabaseService.java  # Executes SQL via JDBC
│   │   │           └── model/
│   │   │               └── QueryRequest.java     # Payload format for APIs
│   │   └── resources/
│   │       └── application.properties            # DB and app config
└── pom.xml
```

---

## 🔄 Control Flow

### CLI Usage:

1. **User Input**: The user types a natural language query into the terminal.
2. **LLM Translation**: `LLMService` sends this to Ollama and retrieves an SQL query.
3. **Query Execution**: `DatabaseService` runs the SQL query on MariaDB.
4. **Result Explanation**: The raw result is sent back to Ollama for explanation.
5. **Output**: The CLI prints both the table result and the natural-language explanation.

---

### API Workflow:

- **POST** `/api/query`  
  - Accepts natural language input
  - Returns: SQL query, execution result, explanation

- **POST** `/api/to-sql`  
  - Converts natural language → SQL  
  - For previewing or validating SQL before running it

- **POST** `/api/execute`  
  - Accepts SQL query → returns raw result

- **POST** `/api/explain`  
  - Accepts SQL output → gets LLM-generated explanation

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- MariaDB running locally or remotely
- Ollama installed with a supported LLM (or access to API)

### Build & Run
```bash
mvn clean install
java -jar target/monza-core.jar
```

For CLI use:
```bash
java -cp target/monza-core.jar monza.devs.App
```

---

## 🧠 Future Plans

- Web frontend with chat interface

---

## 🧙 About Monza

no comment