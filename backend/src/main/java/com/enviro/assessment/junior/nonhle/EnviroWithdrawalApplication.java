package com.enviro.assessment.junior.nonhle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Enviro365 Investments Withdrawal Notice System.
 *
 * This is a Spring Boot application that exposes REST APIs for:
 *  - retrieving investor portfolios
 *  - creating/validating withdrawal notices
 *  - exporting withdrawal statements as CSV
 *
 * Data is persisted in an in-memory H2 database, seeded on startup
 * so the system can be evaluated without any external database setup.
 */
@SpringBootApplication
public class EnviroWithdrawalApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnviroWithdrawalApplication.class, args);
    }

}
