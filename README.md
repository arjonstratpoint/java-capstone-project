# Java Training Final Project

## Introduction

This project is a simple web application built with Spring Boot. It includes features such as user authentication, content management, and a variety of CRUD operations.

## Prerequisites

- Java 17
- Maven
- Postgres or other database (for development)
- H2 Database (for testing)
- Git

## Getting Started

To get started with this project, clone the repository and build the project using Maven.

`git clone https://github.com/arjonstratpoint/java-training-project.git`

### For Development
- Setup the `application.properties`  `spring.datasource` to your own database credentials
- Include your own `private.pem` and `public.pem` files under `main/resources/certs`
- Run or build the application using your IDE or via command line
  - via command line execute `./mvnw spring-boot:run` 

### For Testing
- This project uses H2 Database for testing, configured on test's own `application.properties`
- run the test via command line `./mvnw test`

### For Production and Deployment
- Uses [Railway](https://railway.app/) to deploy repo and Postgres database
- This project has a separate config properties for production `application-production.properties`


## Postman Collection
You can use the provided Postman collection to test the API endpoints. Import the collection into Postman using the following steps:

- Open Postman.
- Click on Import in the top left corner.
- Select the postman_collection.json file located in the root of this repository.
- Click Import.