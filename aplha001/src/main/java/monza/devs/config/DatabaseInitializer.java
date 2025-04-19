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
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Alice", "Frontend Development", Date.valueOf("2024-12-01"), 95000, 25, "American", "Female", 5000, 4);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Bob", "Backend Development", Date.valueOf("2025-01-15"), 105000, 30, "British", "Male", 4500, 5);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Charlie", "System Administration", Date.valueOf("2023-11-20"), 98000, 28, "Canadian", "Male", 4800, 2);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Diana", "Quality Assurance", Date.valueOf("2022-09-10"), 120000, 35, "Indian", "Female", 4700, 3);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Ethan", "DevOps", Date.valueOf("2023-05-25"), 97000, 27, "Australian", "Male", 4900, 5);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Fiona", "UI/UX Design", Date.valueOf("2023-01-10"), 110000, 32, "German", "Female", 4600, 4);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Ron", "Database Administration", Date.valueOf("2011-01-10"), 150000, 40, "English", "Male", 5000, 2);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Aditi", "CEO", Date.valueOf("2010-01-01"), 145000, 38, "Indian", "Female", 5000, 5);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Lohit", "CEO", Date.valueOf("2010-01-01"), 140000, 42, "Indian", "Male", 5000, 6);
            jdbc.update("INSERT INTO employees (name, department, join_date, salary, age, nationality, gender, canteen_money, avg_work_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "Ramesh", "CEO", Date.valueOf("2010-01-01"), 155000, 45, "Indian", "Male", 5000, 4);
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
