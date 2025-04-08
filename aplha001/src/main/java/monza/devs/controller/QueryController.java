package monza.devs.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import monza.devs.service.SqlGenerationService;

@RestController
@RequestMapping("/")
public class QueryController {
    private final SqlGenerationService sqlGenerationService;
    public QueryController(SqlGenerationService sqlGenerationService) {
        this.sqlGenerationService = sqlGenerationService;
    }

    @GetMapping("/helloworld")
    public String sayHello() {
        return "helloworld";
    }

    // 🔁 Full pipeline: NL → SQL → Execute → Explain
    // @PostMapping("/query")
    // public ResponseEntity<Map<String, Object>> fullPipeline(@RequestBody Map<String, String> body) {
    //     String naturalLanguage = body.get("query");

    //     // Step 1: Generate SQL from NL
    //     Map<String, String> sqlMap = toSql(Map.of("query", naturalLanguage)).getBody();
    //     String sql = sqlMap.get("sql");

    //     // Step 2: Execute the SQL
    //     Map<String, Object> resultMap = executeSql(Map.of("sql", sql)).getBody();
    //     Object resultData = resultMap.get("result");

    //     // Step 3: Explain the result
    //     Map<String, String> explanationMap = explainResult(Map.of("result", resultData)).getBody();
    //     String explanation = explanationMap.get("explanation");

    //     // Combine into final response
    //     return ResponseEntity.ok(Map.of(
    //             "sql", sql,
    //             "result", resultData,
    //             "explanation", explanation
    //     ));
    // }


    @PostMapping("/to-sql")
    public ResponseEntity<Map<String, String>> toSql(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        String model = body.get("model");
        String apiUrl = body.get("ollama_api_url");
    
        if (query == null || model == null || apiUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields: 'query', 'model', or 'ollama_api_url'"));
        }
    
        String sql = sqlGenerationService.generateSql(model, apiUrl, query);
    
        return ResponseEntity.ok(Map.of("sql", sql));
    }
    

    // ⚙️ Execute SQL only
    // @PostMapping("/execute")
    // public ResponseEntity<Map<String, Object>> executeSql(@RequestBody Map<String, String> body) {
    //     String sql = body.get("sql");

    //     // TODO: Replace with real DB query result
    //     List<Map<String, Object>> result = List.of(
    //             Map.of("id", 1, "name", "Alice", "join_date", "2024-12-01"),
    //             Map.of("id", 2, "name", "Bob", "join_date", "2025-01-15")
    //     );

    //     return ResponseEntity.ok(Map.of("result", result));
    // }

    // // 📖 Explain result only
    // @PostMapping("/explain")
    // public ResponseEntity<Map<String, String>> explainResult(@RequestBody Map<String, Object> body) {
    //     Object result = body.get("result");

    //     String explanation = "This query finds employees who joined in the last 3 months.";

    //     return ResponseEntity.ok(Map.of("explanation", explanation));
    // }
}
