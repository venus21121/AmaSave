To use Postgres with spring, you need to:

Add dependencies to your build tool.
Create a new Postgres database with psql.
Connect Spring Boot to your Postgres database.
Create a Java entity with Hibernate.
Create a repository with Spring Data JPA.
Create a controller that uses your repository.
C:\Users\venus\PycharmProjects\retail_pricetracker_web_scraping

## How to execute SQL command in Command Prompt: 
Type: `psql -U postgres -h localhost`
Format: psql -U <username> -h <hosename>
Enter password

Connect to the right database 
Type: 
`\c Retail_PriceTracker`
`
CREATE TABLE product
(
	ID SERIAL PRIMARY KEY,
	Title VARCHAR(255), 
	Price DECIMAL(10,2), 
	Img_url TEXT, 
	Product_url TEXT 
);`
`
CREATE TABLE "user" (
	UserID SERIAL PRIMARY KEY,
	UserEmail VARCHAR(50) UNIQUE NOT NULL,
	Password VARCHAR(50) NOT NULL,
	LoginStatus BOOLEAN NOT NULL DEFAULT FALSE
);`

### To change attributes in a table:
`ALTER TABLE "user" RENAME COLUMN "userid" TO user_id;`

### To delete a table:
`DROP TABLE "user";`

### To create an index on user email for an easy search by email: 
`CREATE INDEX idx_user_email ON "user" (user_email);`

### To print out the table: 
`\d "user"`

set client_encoding to UTF8;

show client_encoding;

`\COPY product FROM 'C:\Program Files\PostgreSQL\16\scripts/amazon_data_15_2.csv' WITH (FORMAT CSV, HEADER);`
`\COPY product (Title, Price, Img_url, Product_url) FROM 'C:\Program Files\PostgreSQL\16\scripts/amazon_data_15_2.csv' WITH (FORMAT CSV, HEADER, ENCODING 'UTF-8', DELIMITER ',', NULL '', ESCAPE '"');`

