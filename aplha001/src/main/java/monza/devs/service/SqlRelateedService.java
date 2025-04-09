package monza.devs.service;

import org.springframework.stereotype.Service;

@Service
public class SqlRelateedService {

    private final LLMService llmService;

    public SqlRelateedService(LLMService llmService) {
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

                Generate a valid SINGLE LINE SQL query based on the following natural language request
                and just say the sql query without any explanation or additional text:
                """;

        String fullPrompt = schemaPrompt + naturalLanguageQuery;

        return llmService.getOllamaResponse(MODEL, OLLAMA_API_URL, fullPrompt);
    }

    public String explainSQL(String MODEL, String OLLAMA_API_URL, String userprompt, String query, String sql_output) {

        String fullPrompt = """
                You are an assistant helping users understand database query results.
    
                The database has a table called `employees` with the following schema:
                - `id` (INT, Primary Key, Auto Increment)
                - `name` (VARCHAR): The name of the employee
                - `department` (VARCHAR): The department the employee belongs to
                - `join_date` (DATE): The date the employee joined the company
                - `salary` (INT): The employee's annual salary in USD
    
                The user asked:
                "%s"
    
                Based on this, you generated and ran the following SQL query:
                ```sql
                %s
                ```
    
                The query returned the following output:
                ```json
                %s
                ```
    
                Now, explain this output to the user in simple terms, taking into account their original question.
                """.formatted(userprompt, query, sql_output);
    
        return llmService.getOllamaResponse(MODEL, OLLAMA_API_URL, fullPrompt);
    }
    
}
