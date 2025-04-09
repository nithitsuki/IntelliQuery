package monza.devs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Object executeSql(String SQLQuery) {
        try {
            // Trim and lowercase to check the type of query
            String lowerQuery = SQLQuery.trim().toLowerCase();
    
            if (lowerQuery.startsWith("select")) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(SQLQuery);
    
                if (rows.isEmpty()) {
                    return Map.of("message", "No results.");
                }
    
                return rows; // Ready-to-serialize JSON structure
            } else {
                int updated = jdbcTemplate.update(SQLQuery);
                return Map.of("message", "Query executed. Rows affected: " + updated);
            }
        } catch (Exception e) {
            return Map.of("error", "Error executing query: " + e.getMessage());
        }
    }
    
}
