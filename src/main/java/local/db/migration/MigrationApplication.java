package local.db.migration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MigrationApplication.class, args);
    }

    @Bean
    public CommandLineRunner process(ApplicationContext ctx) {
        return args -> {
            MigrationService migrationService = ctx.getBean(MigrationService.class);
            migrationService.runMigration();
        };
    }
}
