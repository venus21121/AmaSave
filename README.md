# AmaSave Full-Stack Price Tracker

## Overview
AmaSave is a web application that allows users to track the prices of Amazon products and receive alerts when their desired price threshold is met. The app provides a personalized dashboard where users can manage their tracked products and view price history.

## Description
AmaSave enables users to track the prices of Amazon products by adding product URLs or searching for product names. Users can set price alerts for products they are tracking, and the system will continuously monitor product prices, notifying users when a product falls below their desired price.

## Features
- **User Registration & Login**: Secure registration and login with JWT authentication.
- **Product Tracking**: Track products from Amazon by URL or search.
- **Price Alerts**: Set desired price thresholds for tracked products.
- **Price Monitoring**: Continuously monitor prices and receive alerts when they drop below your set threshold.
- **Price History**: View price history for tracked products.
- **Personalized Dashboard**: View and manage your tracked products and alerts in a personalized dashboard.
- **Admin Access**: Admins can manage users and view all tracked products.

## Technologies Used
- **Frontend**: React, React Router, Axios, Tailwind CSS
- **Backend**: Spring Boot, PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Database**: PostgreSQL

## Setting Up the Environment

This repository contains both the frontend and backend for the application.

### Backend Setup (Spring Boot)
The backend is built with Spring Boot and connected to a PostgreSQL database.

1. Clone the repository.
2. Set up the PostgreSQL database.
3. Configure your database connection in `application.properties`.
4. Run the backend using Maven or your IDE.

For detailed instructions on setting up the backend, please check the `backend/README.md`.

### Frontend Setup (React)
The frontend is a React application.

1. Navigate to the `frontend/` directory.
2. Run `npm install` to install dependencies.
3. Run `npm start` to launch the application.

For more details on the frontend, please check the `frontend/README.md`.

### Database Setup
Instructions on how to set up the database and import the SQL dump are included in the `backend/README.md`.

## Additional Information
- More info about APIs and routes can be found in the backend README.
- More info about the React components and routing can be found in the frontend README.
