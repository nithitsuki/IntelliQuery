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
            jdbc.execute("DROP TABLE IF EXISTS employees");

            jdbc.execute("""
                CREATE TABLE employees(
                    id INT PRIMARY KEY,
                    name VARCHAR(100),
                    department VARCHAR(50),
                    join_date DATE,
                    salary INT,
                    age INT,
                    gender VARCHAR(10)
                )
            """);

            System.out.println("✅ Employees table recreated.");

            // 2. Check if the table has data
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);

            // 3. Insert default values if empty
        if (count != null && count == 0)
        {
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                1, "Alice John", "Frontend Development", Date.valueOf("2024-12-01"), 95000, 25, "Female");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                2, "Bob", "Backend Development", Date.valueOf("2025-01-15"), 105000, 30, "Male");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                3, "Charlie", "System Administration", Date.valueOf("2023-11-20"), 98000, 28, "Male");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                4, "Diana", "Quality Assurance", Date.valueOf("2022-09-10"), 120000, 35, "Female");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                5, "Ethan", "DevOps", Date.valueOf("2023-05-25"), 97000, 27, "Male");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                6, "Fiona", "UI/UX Design", Date.valueOf("2023-01-10"), 110000, 32, "Female");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                7, "Ron", "Database Administration", Date.valueOf("2011-01-10"), 150000, 40, "Male");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                8, "Aditi", "CEO", Date.valueOf("2010-01-01"), 145000, 38, "Female");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                9, "Lohit", "CEO", Date.valueOf("2010-01-01"), 140000, 42, "Male");
                jdbc.update("INSERT INTO employees (id, name, department, join_date, salary, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                10, "Ramesh", "CEO", Date.valueOf("2010-01-01"), 155000, 45, "Male");
                System.out.println("✅ Inserted default employee records.");
            } else {
                System.out.println("✅ Employees table already has data (" + count + " rows).");
            }

        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
        }
    }
}
