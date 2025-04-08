package monza.devs.service;

import org.springframework.stereotype.Service;

@Service
public class SqlGenerationService {

    private final LLMService llmService;
    
    public SqlGenerationService(LLMService llmService) {
        this.llmService = llmService;
    }

    // 🧠 Generate SQL using LLM (replace mock with actual LLM call)
    public String generateSql(String MODEL, String OLLAMA_API_URL,String naturalLanguageQuery) {
        
        String schemaPrompt = "Given this database schema, convert the following into an SQL query: ";
        String fullPrompt = schemaPrompt + naturalLanguageQuery;
        
        return llmService.getOllamaResponse(MODEL, fullPrompt, OLLAMA_API_URL);
    }
}
