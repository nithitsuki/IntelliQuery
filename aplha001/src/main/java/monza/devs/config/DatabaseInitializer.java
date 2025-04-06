package monza.devs.config;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void init() {
        try {
            // 1. Create the table if it doesn't exist
            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100),
                    department VARCHAR(50),
                    join_date DATE
                )
            """);
            System.out.println("✅ Employees table ensured.");

            // 2. Check if the table has data
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);

            // 3. Insert default values if empty
            if (count != null && count == 0) {
                jdbc.update("INSERT INTO employees (name, department, join_date) VALUES (?, ?, ?)",
                        "Alice", "HR", Date.valueOf("2024-12-01"));
                jdbc.update("INSERT INTO employees (name, department, join_date) VALUES (?, ?, ?)",
                        "Bob", "IT", Date.valueOf("2025-01-15"));
                jdbc.update("INSERT INTO employees (name, department, join_date) VALUES (?, ?, ?)",
                        "Charlie", "Finance", Date.valueOf("2023-11-20"));
                System.out.println("✅ Inserted default employee records.");
            } else {
                System.out.println("✅ Employees table already has data (" + count + " rows).");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            // e.printStackTrace();
        }
    }
}
