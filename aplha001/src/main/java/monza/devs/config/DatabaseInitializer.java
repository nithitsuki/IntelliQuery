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
                            salary INT,
                            age INT,
                            nationality VARCHAR(50),
                            gender VARCHAR(10),
                            canteen_money INT
                        )
                    """);
            System.out.println("✅ Employees table recreated.");

            // 2. Check if the table has data
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);

            // 3. Insert default values if empty
            if (count != null && count == 0) {
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Alice", "HR", Date.valueOf("2024-12-01"), 55000, 25, "American", "Female", 5000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Bob", "IT", Date.valueOf("2025-01-15"), 72000, 30, "British", "Male", 4500);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Charlie", "Finance", Date.valueOf("2023-11-20"), 65000, 28, "Canadian", "Male", 4800);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Diana", "Engineering", Date.valueOf("2022-09-10"), 85000, 35, "Indian", "Female", 4700);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ethan", "Marketing", Date.valueOf("2023-05-25"), 60000, 27, "Australian", "Male", 4900);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Fiona", "IT", Date.valueOf("2023-01-10"), 70000, 32, "German", "Female", 4600);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ron", "CEO KA BAAP", Date.valueOf("2011-01-10"), 99990000, 19, "English", "Male", 5000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Aditi", "Management", Date.valueOf("2010-01-01"), 1500000, 40, "Indian", "Female", 5000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Lohit", "Management", Date.valueOf("2010-01-01"), 1500000, 42, "Indian", "Male", 5000);
                jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        "Ramesh", "Management", Date.valueOf("2010-01-01"), 1500000, 45, "Indian", "Male", 5000);
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
