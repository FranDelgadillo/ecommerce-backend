package com.shirtcompany.product.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Configuration
@Profile("!test")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final DatabaseClient databaseClient;

    public DataInitializer(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Bean
    ApplicationRunner initData() {
        return args -> {
            log.info("Starting data initialization...");

            databaseClient.sql("SELECT 1")  // Test connection
                    .fetch()
                    .first()
                    .then(initializeSizes())
                    .then(initializeColors())
                    .then(initializeCategories()
                    .then(initializeGenders()))
                    .doOnSuccess(__ -> log.info("Data initialization completed successfully"))
                    .doOnError(e -> log.error("Data initialization failed: {}", e.getMessage()))
                    .subscribe(
                            null,
                            error -> log.error("Error during subscription: ", error),
                            () -> log.info("Subscription completed")
                    );
        };
    }

    private Mono<Void> initializeSizes() {
        return databaseClient.sql("""
        INSERT INTO sizes (id, name) 
        VALUES (1, 'S'), (2, 'M'), (3, 'L'), (4, 'XL')
        ON CONFLICT (id) DO NOTHING
        """)
                .fetch()
                .rowsUpdated()
                .doOnNext(count -> {
                    if (count > 0) log.info("Inserted {} sizes", count);
                    else log.warn("No sizes inserted (already exist?)");
                })
                .then();
    }

    private Mono<Void> initializeColors() {
        return Mono.defer(() ->
                databaseClient.sql("""
                INSERT INTO colors (id, name) 
                VALUES (1, 'Blanco'), (2, 'Negro'), (3, 'Gris'), 
                       (4, 'Beige'), (5, 'Azul'), (6, 'Marron')
                ON CONFLICT (id) DO NOTHING
                """)
                        .fetch()
                        .rowsUpdated()
                        .doOnNext(count -> log.debug("Inserted {} colors", count))
                        .then()
                        .onErrorResume(e -> {
                            log.error("Error initializing colors: {}", e.getMessage());
                            return Mono.error(e);
                        })
        );
    }

    private Mono<Void> initializeCategories() {
        return Mono.defer(() ->
                databaseClient.sql("""
                INSERT INTO categories (id, name) 
                VALUES (1, 'T-Shirt'), (2, 'Hoodie')
                ON CONFLICT (id) DO NOTHING
                """)
                        .fetch()
                        .rowsUpdated()
                        .doOnNext(count -> log.debug("Inserted {} categories", count))
                        .then()
                        .onErrorResume(e -> {
                            log.error("Error initializing categories: {}", e.getMessage());
                            return Mono.error(e);
                        })
        );
    }

    private Mono<Void> initializeGenders() {
        return Mono.defer(() ->
                databaseClient.sql("""
                INSERT INTO genders (id, name) 
                VALUES (1, 'Men'), (2, 'Women')
                ON CONFLICT (id) DO NOTHING
                """)
                        .fetch()
                        .rowsUpdated()
                        .doOnNext(count -> log.debug("Inserted {} genders", count))
                        .then()
                        .onErrorResume(e -> {
                            log.error("Error initializing genders: {}", e.getMessage());
                            return Mono.error(e);
                        })
        );
    }
}