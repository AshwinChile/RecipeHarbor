package com.recipeharbor.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@Slf4j
public class AbstractContainerBaseTest {

    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest")
                .withEnv("MONGO_INITDB_DATABASE", "recipes") ;
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry)
    {

        log.info("MongoDB Host: {}", MONGO_DB_CONTAINER.getHost());
        log.info("MongoDB Port: {}", MONGO_DB_CONTAINER.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri",() -> MONGO_DB_CONTAINER.getReplicaSetUrl("recipes"));
        registry.add("spring.data.mongodb.database", () -> "recipes");
        log.info("MongoDB URL: {}", MONGO_DB_CONTAINER.getReplicaSetUrl());

    }



}
