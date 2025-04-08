package monza.devs.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/")
public class QueryController {

    @GetMapping("/helloworld")
    public String sayHello() {
        return "helloworld";
    }

    // 🔁 Full pipeline: NL → SQL → Execute → Explain
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> fullPipeline(@RequestBody Map<String, String> body) {
        String naturalLanguage = body.get("query");

        // Step 1: Generate SQL from NL
        Map<String, String> sqlMap = toSql(Map.of("query", naturalLanguage)).getBody();
        String sql = sqlMap.get("sql");

        // Step 2: Execute the SQL
        Map<String, Object> resultMap = executeSql(Map.of("sql", sql)).getBody();
        Object resultData = resultMap.get("result");

        // Step 3: Explain the result
        Map<String, String> explanationMap = explainResult(Map.of("result", resultData)).getBody();
        String explanation = explanationMap.get("explanation");

        // Combine into final response
        return ResponseEntity.ok(Map.of(
                "sql", sql,
                "result", resultData,
                "explanation", explanation
        ));
    }

    // 🧠 Generate SQL only
    @PostMapping("/to-sql")
    public ResponseEntity<Map<String, String>> toSql(@RequestBody Map<String, String> body) {
        String query = body.get("query");

        // TODO: Replace with actual LLM call
        String sql = "SELECT * FROM employees WHERE join_date >= CURDATE() - INTERVAL 3 MONTH";

        return ResponseEntity.ok(Map.of("sql", sql));
    }

    // ⚙️ Execute SQL only
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeSql(@RequestBody Map<String, String> body) {
        String sql = body.get("sql");

        // TODO: Replace with real DB query result
        List<Map<String, Object>> result = List.of(
                Map.of("id", 1, "name", "Alice", "join_date", "2024-12-01"),
                Map.of("id", 2, "name", "Bob", "join_date", "2025-01-15")
        );

        return ResponseEntity.ok(Map.of("result", result));
    }

    // 📖 Explain result only
    @PostMapping("/explain")
    public ResponseEntity<Map<String, String>> explainResult(@RequestBody Map<String, Object> body) {
        Object result = body.get("result");

        // TODO: Replace with actual LLM explanation
        String explanation = "This query finds employees who joined in the last 3 months.";

        return ResponseEntity.ok(Map.of("explanation", explanation));
    }
}
