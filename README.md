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
2. Run the below command to start the mongodb database container:
   _**docker run -d -p 27017:27017 --name mongodb_container mongo:latest**_
3. To start the application, run the below command:
 _**mvn spring-boot:run**_
4. To run only the unit test cases, run the below command:
 _**mvn test**_
5. To run the integration test cases and unit test cases, run the below command:
 _**mvn verify**_
6. To access the API' documentation, open the below URL in your browser:
 _**http://localhost:8080/swagger-ui.html**_
7. 
