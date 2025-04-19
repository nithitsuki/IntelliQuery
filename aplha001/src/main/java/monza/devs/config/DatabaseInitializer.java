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
                CREATE TABLE employees (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100),
                    department VARCHAR(50),
                    join_date DATE,
                    salary INT,
                    age INT,
                    nationality VARCHAR(50),
                    gender VARCHAR(10),
                    phone_number VARCHAR(15),
                    location VARCHAR(100)
                )
            """);

            System.out.println("✅ Employees table recreated.");

            // 2. Check if the table has data
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);

            // 3. Insert default values if empty
            if (count != null && count == 0) {
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Alice", "Frontend Development", Date.valueOf("2024-12-01"), 95000, 25, "American", "Female", "123456789", "Indiranagar");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Bob", "Backend Development", Date.valueOf("2025-01-15"), 105000, 30, "British", "Male", "234567890", "Whitefield");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Charlie", "System Administration", Date.valueOf("2023-11-20"), 98000, 28, "Canadian", "Male", "345678901", "Koramangla");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Diana", "Quality Assurance", Date.valueOf("2022-09-10"), 120000, 35, "Indian", "Female", "456789012", "Whitefield");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ethan", "DevOps", Date.valueOf("2023-05-25"), 97000, 27, "Australian", "Male", "567890123", "Indiranagar");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Fiona", "UI/UX Design", Date.valueOf("2023-01-10"), 110000, 32, "German", "Female", "678901234", "Whitefield");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ron", "Database Administration", Date.valueOf("2011-01-10"), 150000, 40, "English", "Male", "789012345", "Koramangla");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Aditi", "CEO", Date.valueOf("2010-01-01"), 145000, 38, "Indian", "Female", "890123456", "Koramangla");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Lohit", "CEO", Date.valueOf("2010-01-01"), 140000, 42, "Indian", "Male", "901234567", "Indiranagar");
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, phone_number, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ramesh", "CEO", Date.valueOf("2010-01-01"), 155000, 45, "Indian", "Male", "012345678", "Whitefield");

                System.out.println("✅ Inserted default employee records.");
            } else {
                System.out.println("✅ Employees table already has data (" + count + " rows).");
            }

        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
        }
    }
}
