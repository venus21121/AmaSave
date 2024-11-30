### Entity Layer:

The entity layer consists of classes that represent the domain model or business objects of your application.
Each entity class typically maps to a table in the database.
Duties:
Define the structure and behavior of domain objects.
Represent data entities and their relationships.
Provide a mapping between Java objects and database tables.
Example: In a Spring Boot application using JPA, entity classes are annotated with @Entity and represent domain entities like User, Product, Order, etc.

### Repository Layer:

The repository layer is responsible for interacting with the database.
It includes repository interfaces that define methods for performing CRUD (Create, Read, Update, Delete) operations on entities.
Duties:
Encapsulate data access logic.
Provide methods for querying and manipulating data.
Abstract away the details of database interaction from the rest of the application.
Example: In a Spring Boot application using Spring Data JPA, repository interfaces extend JpaRepository and provide methods for accessing and manipulating data from the database, such as findById(), save(), deleteById(), etc.

### Service Layer:

The service layer contains business logic and application-specific functionality.
It orchestrates interactions between the presentation layer and the data access layer.
Duties:
Implement business rules and logic.
Coordinate transactions and interactions between multiple components.
Enforce security policies and access control.
Example: In a Spring Boot application, service classes contain methods for implementing business logic and coordinating interactions between controllers and repositories. For example, a UserService might contain methods for user authentication, user registration, user profile management, etc.

### Controller Layer:

The controller layer handles incoming HTTP requests, processes them, and sends back appropriate responses.
It acts as an entry point for handling client requests and delegates the execution to the service layer.
Duties:
Receive and parse incoming requests.
Invoke appropriate methods in the service layer to perform business logic.
Construct and send HTTP responses back to the client.

### UserController

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUserEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setLoginStatus(true);
            userRepository.save(user1); // save the changes to the database
            return ResponseEntity.ok("User logged in successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
- When a POST request is made to the `/login` endpoint, the client typically sends data in the request body, commonly formatted as JSON.
- To handle this data effectively, we need a `LoginRequest` class that acts as a DTO (Data Transfer Object) to represent the structure of the JSON data sent by the client.
- The DTO class should have both setter and getter methods to ensure proper serialization and deserialization.
- Having setter and getter methods in the DTO class allows Spring to bind the request body to the `LoginRequest` object, facilitating the deserialization process and enabling access to the data sent by the client in a structured manner.

**Optional** represents an Optional instance that may contain a User object retrieved from the database. The method `findByUserEmailAndPassword` returns an `Optional` instead of a `User` directly. This is because there might not be a user with the provided email and password in the database.

**How Optional is typically used:**
- If a value is present, you can retrieve it using the `get()` method.
- You can check if a value is present using the `isPresent()` method.
- You can perform actions based on whether a value is present or not using methods like `ifPresent()`, `orElse()`, `orElseGet()`, etc.

### Return value
The ResponseEntity class in Spring provides various methods to create different types of HTTP responses. Here are some commonly used methods:
- `ok()`: Creates a response with status code `200 OK`.
- `created()`: Creates a response with status code `201 Created`. This is typically used when creating a new resource (e.g., after a successful user registration).
- `badRequest()`: Creates a response with status code `400 Bad Request`. This is used to indicate that the client's request is invalid.
- `unauthorized()`: Creates a response with status code `401 Unauthorized`. This is used to indicate that the client needs to authenticate to access the resource.
- `status(HttpStatus status)`: Creates a response with the specified HTTP status code.
    - Example: `ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!")`

## Check you API Calls
- use `postman` to check any api calls with your localhost endpoint 


| HTTP Verb | CRUD   | Entire Collection (e.g. /customers)                                        | Specific Item (e.g. /customers/{id})                                                                                                                                                                                                                                                             |
|-----------|--------|--------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST      | Create | 201 (Created), 'Location' header with link to /customers/{id} containing new ID. | 404 (Not Found), 409 (Conflict) if resource already exists.                                                                                                                                                                                                                                      |
| GET       | Read   | 200 (OK), list of customers. Use pagination, sorting and filtering to navigate big lists. | 200 (OK), single customer. 404 (Not Found), if ID not found or invalid.                                                                                                                                                                                                                         |
| PUT       | Update/Replace | 405 (Method Not Allowed), unless you want to update/replace every resource in the entire collection. | 200 (OK) or 204 (No Content). 404 (Not Found), if ID not found or invalid.                                                                                                                                                                                                                      |
| PATCH     | Update/Modify  | 405 (Method Not Allowed), unless you want to modify the collection itself.         | 200 (OK) or 204 (No Content). 404 (Not Found), if ID not found or invalid.                                                                                                                                                                                                                      |
| DELETE    | Delete | 405 (Method Not Allowed), unless you want to delete the whole collection—not often desirable. | 200 (OK). 404 (Not Found), if ID not found or invalid.                                                                                                                                                                                                                                           |


### Product old version:
package com.example.retailpricetracker;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private BigDecimal price;

    private String imgUrl;

    private String productUrl;

    // Empty constructor
    public Product() {

    }
    // Constructors with all parameters
    public Product(Long id, String title, BigDecimal price, String imgUrl, String productUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imgUrl = imgUrl;
        this.productUrl = productUrl;
    }



    // getters,

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

// setters,

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

// etc.

}
