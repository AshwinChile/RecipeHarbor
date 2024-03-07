package com.recipeharbor.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeharbor.entity.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class MongoConfig {
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.mongodb.database}")
    private String collectionName;

    public MongoConfig(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeMongoDB() throws IOException {
        createCollectionAndInsertData();
    }

    private void createCollectionAndInsertData() throws IOException {
        // Create a collection if not exists
        if (mongoTemplate.collectionExists(collectionName) && mongoTemplate.getCollection(collectionName).countDocuments() == 0) {
            mongoTemplate.createCollection(collectionName);
            // Add initial data to the collection
            ClassPathResource resource = new ClassPathResource("data.json");
            List<Recipe> recipes = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Recipe>>() {});
            mongoTemplate.insertAll(recipes);
        }
    }
}
