
# AmaSave - Backend Setup

This guide will walk you through the steps needed to set up the database for the AmaSave backend application.

AmaSave uses PostgreSQL as the database management system. Follow these steps to set up the database, configure the Spring Boot application, and get everything up and running.

---

## Prerequisites

Before setting up the database and backend, ensure that you have the following installed:

- [PostgreSQL](https://www.postgresql.org/download/) (version 12 or above)
- Java 11 or higher
- [Maven](https://maven.apache.org/install.html) (if you are using Maven)
- IDE (like IntelliJ IDEA or Eclipse) or a terminal to run commands

---

## Step 1: Create a New Database in PostgreSQL

If you haven't set up a PostgreSQL database yet, follow these steps:

1. Open the PostgreSQL command line or **pgAdmin**.
2. Create a new database for AmaSave. You can use the following SQL command to do so:

   ```sql
   CREATE DATABASE AmaSaveDB;

## Step 2: Configure the application.properties File
Next, you need to configure the Spring Boot application to connect to the PostgreSQL database. This is done in the application.properties file located in src/main/resources/.

Create the application.properties file and update it with the following database configuration:

````
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/AmaSaveDB
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Token Configuration
security.jwt.secret-key=your-generated-secret-key
security.jwt.expiration-time=3600000 # 1 hour in milliseconds
````

## Step 3: Run application first (create Tables)
Now that you have configured the database and application properties, you can run the Spring Boot application. This will automatically connect to the database and, if necessary, create the required tables based on your entity classes.
## Step 4: Restore Product Data from SQL Dump
Once you have created the database, you need to restore the product table and its data. We have provided a SQL dump file (productData.sql) that contains the product data.

   ``` bash
   pg_restore -U postgres -d AmaSaveDB --clean -v "path/to/productData.sql"
   ```
## Step 5: Run application
Once the application is running, you should be able to access the API and see the product data in your PostgreSQL database.
