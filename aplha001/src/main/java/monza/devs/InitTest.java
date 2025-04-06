package monza.devs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import monza.devs.config.DatabaseInitializer;

public class InitTest {
    public static void main(String[] args) {
        // Manually create a Spring context with the database config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("dataSource", DriverManagerDataSource.class, () -> {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.mariadb.jdbc.Driver");
            ds.setUrl("jdbc:mariadb://localhost:3306/monza");
            ds.setUsername("root");
            ds.setPassword("toor"); // <-- change this
            return ds;
        });
        context.registerBean(JdbcTemplate.class, () -> new JdbcTemplate(context.getBean(DriverManagerDataSource.class)));
        context.registerBean(DatabaseInitializer.class);
        context.refresh(); // Triggers @PostConstruct

        System.out.println("✅ Done!");
    }
}
