package monza.devs.service;

import org.springframework.stereotype.Service;

@Service
public class SqlGenerationService {

    private final LLMService llmService;

    public SqlGenerationService(LLMService llmService) {
        this.llmService = llmService;
    }

    // 🧠 Generate SQL using LLM (replace mock with actual LLM call)
    public String generateSql(String MODEL, String OLLAMA_API_URL, String naturalLanguageQuery) {

        String schemaPrompt = """
                You are provided with a database that contains a table named `employees` with the following columns:

                - `id` (INT, Primary Key, Auto Increment)
                - `name` (VARCHAR): The name of the employee
                - `department` (VARCHAR): The department the employee belongs to
                - `join_date` (DATE): The date the employee joined the company
                - `salary` (INT): The employee's annual salary in USD

                Generate a valid SQL query based on the following natural language request
                and just say the sql query without any explanation or additional text:
                """;

        String fullPrompt = schemaPrompt + naturalLanguageQuery;

        return llmService.getOllamaResponse(MODEL, OLLAMA_API_URL, fullPrompt);
    }
}
