# Recipe Harbor

This application provides a set of REST API's to store, retrieve, update, fetch and filter recipes.

**Pre-requisites:**
1. Java 17
2. Docker
3. Maven
4. Any IDE (IntelliJ, Eclipse, etc)
5. Postman (or any other API testing tool)

**Steps to run the application:**
1. Ensure docker is running on your machine.
2. Clone the repository to your local machine and open the project in your IDE.
3. Run the below command to start the mongodb database container:
   _**docker run -d -p 27017:27017 --name mongodb_container mongo:latest**_
4. To start the application, run the below command from the root of the project directory:
 _**mvn spring-boot:run**_
5. To run only the unit test cases, run the below command:
 _**mvn test**_
6. To run the integration test cases and unit test cases, run the below command:
 _**mvn verify**_
7. To access the API' documentation, open the below URL in your browser:
 _**http://localhost:8080/swagger-ui.html**_
8. A postman collection is also available in the project root directory with the name "RecipeHarbor.postman_collection.json". 
   You can import this collection in your postman and start testing the API's. 
9. Some seed data gets added to the local database on application start, this should help with testing the API's.

# Architecture:

This application can be built in both relational or non-relational databases.
For this project, I have used MongoDB as the database. The application is built using Spring Boot and Java 17.

The reason for choosing a NoSql database was primarily because of full text search capabilities of Mongo and that was one of the requirements of the task.
Additionally, Mongo gives the flexibility to store complex data structures.
The application uses the Spring Data MongoDB library to interact with the database.

The real data-modeling decision can only be made after understanding the exact data and use case requirements.
Here I have used a simplistic data model to store the recipes and their ingredients and choose to store the ingredients as a list of strings, 
but it could also be a separate collection with a reference to the recipe.


**The application is divided into 3 layers:**
1. Controller Layer: This layer is responsible for handling the incoming requests and sending the response back to the client.
2. Service Layer: This layer is responsible for handling the business logic and calling the repository layer to perform the CRUD operations.
3. Repository Layer: This layer is responsible for interacting with the database and performing the CRUD operations.

**Other Considerations:**
1. Apart from these layers, there are also DTO's, Exception and Config classes handling classes.
2. Logging has been implemented using SLF4J and default configuration is INFO level.
3. The application is also integrated with Swagger for API documentation.
4. The application is also integrated with Lombok to reduce boilerplate code.
5. The application is also integrated with Junit and Mockito for unit and integration testing.
6. The integration tests use mongo test containers to spin up a temporary mongo database for testing purposes and are hence completely isolated from the local database.