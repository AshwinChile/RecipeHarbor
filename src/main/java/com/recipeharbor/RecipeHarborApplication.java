package com.recipeharbor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
public class RecipeHarborApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeHarborApplication.class, args);
	}

}
