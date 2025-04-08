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
            // 1. Drop and recreate the employees table (optional but clean)
            // You can also use TRUNCATE if you're sure the schema is already set
            jdbc.execute("DROP TABLE IF EXISTS employees");

            jdbc.execute("""
                        CREATE TABLE employees (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100),
                            department VARCHAR(50),
                            join_date DATE,
                            salary INT
                        )
                    """);
            System.out.println("✅ Employees table recreated.");

            // 2. Check if the table has data
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);

            // 3. Insert default values if empty
            if (count != null && count == 0) {
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Alice", "HR", Date.valueOf("2024-12-01"), 55000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Bob", "IT", Date.valueOf("2025-01-15"), 72000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Charlie", "Finance", Date.valueOf("2023-11-20"), 65000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Diana", "Engineering", Date.valueOf("2022-09-10"), 85000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Ethan", "Marketing", Date.valueOf("2023-05-25"), 60000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary) VALUES (?, ?, ?, ?)",
                        "Fiona", "IT", Date.valueOf("2023-01-10"), 70000);
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
