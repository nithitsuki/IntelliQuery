package monza.devs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import monza.devs.service.DatabaseService;
import monza.devs.service.LLMService;
import monza.devs.service.SqlRelateedService;

@RestController
@RequestMapping("/")
public class QueryController {

    private final SqlRelateedService sqlRelateedService;
    private final LLMService llmService;
    private final DatabaseService databaseService;

    public QueryController(
            SqlRelateedService sqlRelateedService,
            LLMService llmService,
            DatabaseService databaseService) {
        this.sqlRelateedService = sqlRelateedService;
        this.llmService = llmService;
        this.databaseService = databaseService;
    }

    @GetMapping("/helloworld")
    public String sayHello() {
        return "helloworld";
    }

    // 🔁 Full pipeline: NL → SQL → Execute → Explain
    // @PostMapping("/query")
    // public ResponseEntity<Map<String, Object>> fullPipeline(@RequestBody
    // Map<String, String> body) {
    // String naturalLanguage = body.get("query");

    // // Step 1: Generate SQL from NL
    // Map<String, String> sqlMap = toSql(Map.of("query",
    // naturalLanguage)).getBody();
    // String sql = sqlMap.get("sql");

    // // Step 2: Execute the SQL
    // Map<String, Object> resultMap = executeSql(Map.of("sql", sql)).getBody();
    // Object resultData = resultMap.get("result");

    // // Step 3: Explain the result
    // Map<String, String> explanationMap = explainResult(Map.of("result",
    // resultData)).getBody();
    // String explanation = explanationMap.get("explanation");

    // // Combine into final response
    // return ResponseEntity.ok(Map.of(
    // "sql", sql,
    // "result", resultData,
    // "explanation", explanation
    // ));
    // }

    @PostMapping("/to-sql")
    public ResponseEntity<Map<String, String>> toSql(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        String model = body.get("model");
        String apiUrl = body.get("ollama_api_url");

        if (query == null || model == null || apiUrl == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required fields: 'query', 'model', or 'ollama_api_url'"));
        }

        String sql = sqlRelateedService.generateSql(model, apiUrl, query);

        return ResponseEntity.ok(Map.of("sql", sql));
    }

    // ⚙️ Execute SQL only
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeSql(@RequestBody Map<String, String> body) {
        String sql = body.get("sql-query");

        if (sql == null || sql.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Missing 'sql-query' in request body"));
        }

        Object result = databaseService.executeSql(sql);
        Map<String, Object> response = new HashMap<>();

        if (result instanceof Map && ((Map<?, ?>) result).containsKey("error")) {
            // It's an error result
            response.put("error", ((Map<?, ?>) result).get("error"));
            return ResponseEntity.badRequest().body(response);
        }

        if (result instanceof List) {
            // It's SELECT query result
            response.put("result", result);
        } else {
            // It's an update/insert/delete
            response.put("message", ((Map<?, ?>) result).get("message"));
        }

        return ResponseEntity.ok(response);
    }

    // 📖 Explain result only
    @PostMapping("/explain")
    public ResponseEntity<Map<String, String>> explainResult(@RequestBody Map<String, String> body) {
        String model = body.get("model");
        String apiUrl = body.get("ollama_api_url");
        String userprompt = body.get("prompt");
        String sql_output = body.get("sql_output");
        String sqlquery = body.get("query");

        if (sqlquery == null || model == null || apiUrl == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required fields: 'query', 'model', or 'ollama_api_url'"));
        }

        String explaination = sqlRelateedService.explainSQL(model, apiUrl, userprompt, sqlquery, sql_output);

        return ResponseEntity.ok(Map.of("explaination", explaination));

    }


}
